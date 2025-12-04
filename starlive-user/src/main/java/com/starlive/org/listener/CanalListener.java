package com.starlive.org.listener;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlive.org.mapper.VideoMapper;
import com.starlive.org.util.RedisUtil;
import com.starlive.org.vo.WorkInfoListVo;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class CanalListener implements InitializingBean {

    private final static int BATCH_SIZE = 1000;
    private final static int Time=60;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private CanalConnector connector;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisUtil redisUtil;
    private final VideoMapper videoMapper;
    @Autowired
    public CanalListener(RedisUtil redisUtil, VideoMapper videoMapper) {
        this.redisUtil = redisUtil;
        this.videoMapper = videoMapper;
    }


    @Override
    public void afterPropertiesSet() {
        // 创建连接
        connector = CanalConnectors.newSingleConnector(new InetSocketAddress("127.0.0.1", 11111), "example", "root", "root");
        // 开始监听
        executorService.submit(this::startListening);
    }

    private void startListening() {
        try {
            // 打开连接
            connector.connect();
            // 订阅数据库表
            connector.subscribe("starlive.video"); // 替换为实际的数据库和表名
            connector.rollback();

            while (true) {
                // 获取指定数量的数据
                Message message = connector.getWithoutAck(BATCH_SIZE);
                long batchId = message.getId();
                int size = message.getEntries().size();

                // 如果没有数据
                if (batchId == -1 || size == 0) {
                    try {
                        Thread.sleep(2000); // 暂停2秒
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // 恢复中断状态
                    }
                } else {
                    // 处理数据
                    printEntry(message.getEntries());
                }
                // 确认处理的消息
                connector.ack(batchId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connector.disconnect();
        }
    }

    /**
     * 打印 Canal Server 解析 binlog 获得的实体类信息
     */
    private  void printEntry(List<CanalEntry.Entry> entries) throws JsonProcessingException {

        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                //开启/关闭事务的实体类型
                continue;
            }

            CanalEntry.RowChange rowChange;
            //RowChange对象，包含了一行数据变化的所有特征
            //比如isDdl 是否是ddl变更操作 sql 具体的ddl sql beforeColumns afterColumns 变更前后的数据字段等等
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error, data:" + entry.toString(), e);
            }

            //获取操作类型：insert/update/delete类型
            CanalEntry.EventType eventType = rowChange.getEventType();

            //判断是否是DDL语句
            if (rowChange.getIsDdl()) {
                System.out.println("是DDL语句: " + rowChange.getSql());
            }


            switch (eventType) {
                case INSERT:
                    //System.out.println("库名：" + entry.getHeader().getSchemaName() + " -- 表名：" + entry.getHeader().getTableName() + " 新增数据");
                    // 是新增语句，业务处理。如果新增的时候数据没有发生变化的情况下，是不会被执行
                    for (CanalEntry.RowData rowData : rowChange.getRowDatasList()){
                        String id=rowData.getAfterColumnsList().get(1).getValue();
                        Long userId=Long.parseLong(id);
                        String cacheKey = "workInfo:" + id;
                        String workInfoJson= (String) redisUtil.get(cacheKey);
                        if (workInfoJson != null) {
                            WorkInfoListVo workInfoListVo=objectMapper.readValue(workInfoJson, WorkInfoListVo.class);
                            workInfoListVo.setWorknum(videoMapper.getVideoCount(userId));
                            workInfoJson= objectMapper.writeValueAsString(workInfoListVo);
                            if(workInfoJson!=null){
                                //1.redis过期时间30分钟
                                redisUtil.set(cacheKey, workInfoJson,Time, TimeUnit.SECONDS);
                            }else
                                //2.空值处理
                                redisUtil.set(cacheKey,new WorkInfoListVo(),10, TimeUnit.SECONDS);

                        }

                    }
                    //rowChange.getRowDatasList().forEach(rowData -> printColumn(rowData.getAfterColumnsList()));
                    break;
                case DELETE:
                    // 是删除语句，业务处理。如果删除的时候是没有数据的情况下，是不会被执行
                    rowChange.getRowDatasList().forEach(rowData -> printColumn(rowData.getBeforeColumnsList()));
                    break;
                case UPDATE:
                    //....
                    for (CanalEntry.RowData rowData : rowChange.getRowDatasList()){
                        String id=rowData.getAfterColumnsList().get(1).getValue();
                        Long userId=Long.parseLong(id);
                        String cacheKey = "workInfo:" + id;
                        String workInfoJson= (String) redisUtil.get(cacheKey);
                        if (workInfoJson != null) {
                            WorkInfoListVo workInfoListVo=objectMapper.readValue(workInfoJson, WorkInfoListVo.class);
                            workInfoListVo.setWorkInfoList(videoMapper.getWorkInfo(userId));
                            workInfoJson= objectMapper.writeValueAsString(workInfoListVo);
                            if(workInfoJson!=null){
                                //1.redis过期时间30分钟
                                redisUtil.set(cacheKey, workInfoJson,Time, TimeUnit.SECONDS);
                            }else
                                //2.空值处理
                                redisUtil.set(cacheKey,new WorkInfoListVo(),10, TimeUnit.SECONDS);

                        }

                    }

                    break;
                default:
                    break;
            }
        }
    }


    private static void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }

    // 关闭 ExecutorService 在应用停止时
    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }
}
