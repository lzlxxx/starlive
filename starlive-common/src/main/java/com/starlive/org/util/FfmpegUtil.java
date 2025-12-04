package com.starlive.org.util;

import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.filters.DrawtextFilter;
import ws.schild.jave.filters.helpers.Color;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.info.VideoSize;
import ws.schild.jave.process.ProcessWrapper;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;
import ws.schild.jave.progress.EchoingEncoderProgressListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author ruibo.duan, <1573434995@qq.com>
 * @since 2021/7/22
 */

public class FfmpegUtil {

    /**
     * 通过本地路径获取多媒体文件信息(宽，高，时长，编码等)
     *
     * @param localPath 本地路径
     * @return MultimediaInfo 对象,包含 (宽，高，时长，编码等)
     * @throws EncoderException
     */
    public  MultimediaInfo getMultimediaInfo(String localPath) {
        MultimediaInfo multimediaInfo = null;
        try {
            multimediaInfo = new MultimediaObject(new File(localPath)).getInfo();
        } catch (EncoderException e) {
            System.out.println("获取多媒体文件信息异常");
            e.printStackTrace();
        }
        return multimediaInfo;
    }

    /**
     * 通过URL获取多媒体文件信息
     *
     * @param url 网络url
     * @return MultimediaInfo 对象,包含 (宽，高，时长，编码等)
     * @throws EncoderException
     */
    public  MultimediaInfo getMultimediaInfoFromUrl(String url) {
        MultimediaInfo multimediaInfo = null;
        try {
            multimediaInfo = new MultimediaObject(new URL(url)).getInfo();
        } catch (Exception e) {
            System.out.println("获取多媒体文件信息异常");
            e.printStackTrace();
        }
        return multimediaInfo;
    }

    private static final int SAMPLING_RATE = 16000;
    private static final int SINGLE_CHANNEL = 1;

    /**
     * 音频格式化为wav,并设置单声道和采样率
     *
     * @param url 需要转格式的音频,本地路径
     * @param targetPath 格式化后要保存的目标路径
     */
    public  boolean formatAudioLocal(String url, String targetPath) {
        File target = new File(targetPath);
        MultimediaObject multimediaObject;
        try {
            // 本地文件：
            multimediaObject = new MultimediaObject(new File(url));
            // 音频参数
            AudioAttributes audio = new AudioAttributes();
            // 采样率
            audio.setSamplingRate(SAMPLING_RATE);
            // 单声道
            audio.setChannels(SINGLE_CHANNEL);
            Encoder encoder = new Encoder();
            EncodingAttributes attrs = new EncodingAttributes();
            // 输出格式
            attrs.setOutputFormat("wav");
            attrs.setAudioAttributes(audio);
            encoder.encode(multimediaObject, target, attrs);
            return true;
        } catch (Exception e) {
            System.out.println("格式化音频异常");
            return false;
        }
    }
    /**
     * 音频格式化为wav,并设置单声道和采样率
     *
     * @param url 需要转格式的音频，url
     * @param targetPath 格式化后要保存的目标路径
     */
    public  boolean formatAudioUrl(String url, String targetPath) {
        File target = new File(targetPath);
        MultimediaObject multimediaObject;
        try {
            //url路径
            multimediaObject = new MultimediaObject(new URL(url));
            // 音频参数
            AudioAttributes audio = new AudioAttributes();
            // 采样率
            audio.setSamplingRate(SAMPLING_RATE);
            // 单声道
            audio.setChannels(SINGLE_CHANNEL);
            Encoder encoder = new Encoder();
            EncodingAttributes attrs = new EncodingAttributes();
            // 输出格式
            attrs.setOutputFormat("wav");
            attrs.setAudioAttributes(audio);
            encoder.encode(multimediaObject, target, attrs);
            return true;
        } catch (Exception e) {
            System.out.println("格式化音频异常");
            return false;
        }
    }


    /**
     * 视频格式化,本地路径
     *
     * @param url
     * @param targetPath
     * @return
     */
    public  boolean formatToMp4Local(String url, String targetPath,String format) {
        File target = new File(targetPath);
        MultimediaObject multimediaObject;
        if(format.equals("mp4")||format.equals("mov")||format.equals("mkv")||format.equals("avi")){
            try {
                //本地文件
                multimediaObject = new MultimediaObject(new File(url));
                EncodingAttributes attributes = new EncodingAttributes();
                // 设置视频的音频参数
                AudioAttributes audioAttr = new AudioAttributes();
                attributes.setAudioAttributes(audioAttr);
                audioAttr.setChannels(2);
                audioAttr.setCodec("aac");
                audioAttr.setBitRate(128000);
                audioAttr.setSamplingRate(44100);
                // 设置视频的视频参数
                VideoAttributes videoAttr = new VideoAttributes();
                // 设置帧率
                videoAttr.setCodec("libx264");
                videoAttr.setBitRate(2 * 1024 * 1024);
                videoAttr.setSize(new VideoSize(1080, 720));
                videoAttr.setFaststart(true);
                videoAttr.setFrameRate(30);
                attributes.setVideoAttributes(videoAttr);
                // 设置输出格式
                attributes.setOutputFormat(format);
                Encoder encoder = new Encoder();
                encoder.encode(multimediaObject, target, attributes,new EchoingEncoderProgressListener());
                return true;
            } catch (Exception e) {
                System.out.println("格式化视频异常");
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    /**
     * 视频格式化,url
     *
     * @param url
     * @param targetPath
     * @return
     */
    public  boolean formatToMp4Url(String url, String targetPath,String format) {
        File target = new File(targetPath);
        MultimediaObject multimediaObject;
        if(format.equals("mp4")||format.equals("mov")||format.equals("mkv")||format.equals("avi")){
            try {
                //url路径
                multimediaObject = new MultimediaObject(new URL(url));
                EncodingAttributes attributes = new EncodingAttributes();
                // 设置视频的音频参数
                AudioAttributes audioAttr = new AudioAttributes();
                attributes.setAudioAttributes(audioAttr);
                audioAttr.setChannels(2);
                audioAttr.setCodec("aac");
                audioAttr.setBitRate(128000);
                audioAttr.setSamplingRate(44100);
                // 设置视频的视频参数
                VideoAttributes videoAttr = new VideoAttributes();
                // 设置帧率
                videoAttr.setCodec("libx264");
                videoAttr.setBitRate(2 * 1024 * 1024);
                videoAttr.setSize(new VideoSize(1080, 720));
                videoAttr.setFaststart(true);
                videoAttr.setFrameRate(30);
                attributes.setVideoAttributes(videoAttr);
                // 设置输出格式
                attributes.setOutputFormat("mp4");
                Encoder encoder = new Encoder();
                encoder.encode(multimediaObject, target, attributes,new EchoingEncoderProgressListener());
                return true;
            } catch (Exception e) {
                System.out.println("格式化视频异常");
                e.printStackTrace();
                return false;
            }
        }
        return false;

    }

    /**
     * 获取视频缩略图 获取视频第0秒的第一帧图片
     *
     * <p>执行的ffmpeg 命令为： ffmpeg -i 视频文件路径 -ss 指定的秒数 生成文件的全路径地址
     *
     * @param localPath 本地路径
     * @param targetPath 存放的目标路径
     * @return
     */
    public  boolean getTargetThumbnail(String localPath, String targetPath,String arg) {
        // 该方法基本可作为执行ffmpeg命令的模板方法，之后的几个方法与此类似
        try {
            try(ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor()){
                ffmpeg.addArgument("-i");
                ffmpeg.addArgument(localPath);
                ffmpeg.addArgument("-ss");
                // 此处可自定义视频的秒数
                ffmpeg.addArgument(arg);
                ffmpeg.addArgument(targetPath);
                ffmpeg.execute();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                    blockFfmpeg(br);
                }}
        } catch (IOException e) {
            System.out.println("获取视频缩略图失败");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 等待命令执行成功，退出
     *
     * @param br
     * @throws IOException
     */
    private  void blockFfmpeg(BufferedReader br) throws IOException {
        String line;
        // 该方法阻塞线程，直至合成成功
        while ((line = br.readLine()) != null) {
            doNothing(line);
        }
    }

    /**
     * 打印日志
     *
     * @param line
     */
    private  void doNothing(String line) {
        // 仅用于观察日志
        System.out.println("+++++++++++++++++"+line);
    }

    /**
     * 视频增加字幕.软字幕
     *
     * @param originVideoPath 原视频地址
     * @param targetVideoPath 目标视频地址
     * @param srtPath 固定格式的srt文件地址或存储位置，字母文件名： xxx.srt
     * @return
     * @throws Exception
     */
    public  boolean addSubtitle(String originVideoPath, String srtPath, String targetVideoPath) {
        ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
        try {
            // 添加输入视频文件
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(originVideoPath);

            // 添加字幕文件
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(srtPath);

            // 设置复制流避免重新编码
            ffmpeg.addArgument("-c");
            ffmpeg.addArgument("copy");

            // 指定输出文件路径
            ffmpeg.addArgument(targetVideoPath);

            // 执行命令
            ffmpeg.execute();

            // 检查执行是否有错误输出
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                blockFfmpeg(errorReader);
            }

            System.out.println("字幕添加成功: " + targetVideoPath);
            return true;

        } catch (IOException e) {
            System.err.println("字幕添加失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        finally {
            // 释放资源
            if (ffmpeg != null) {
                ffmpeg.destroy();
            }
        }
    }

    /**
     * 给视频添加文字水印
     *
     * @param originVideoPath 原始视频路径
     * @param textWaterMark 水印文字
     * @param targetVideoPath 输出视频路径
     * @return 是否添加成功
     */
    public  boolean addTextWatermark(String originVideoPath, String textWaterMark,String targetVideoPath) {
        ProcessWrapper ffmpeg = null;
        try {
            // 创建 FFmpeg 命令执行器
            ffmpeg = new DefaultFFMPEGLocator().createExecutor();

            // 输入原始视频路径
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(originVideoPath);

            // 设置视频编码（固定为 libx264，压缩质量高，兼容性好）
            ffmpeg.addArgument("-c:v");
            ffmpeg.addArgument("libx264");

            // 设置音频编码（保持原样）
            ffmpeg.addArgument("-c:a");
            ffmpeg.addArgument("aac");

            // 固定视频分辨率（例如 1280x720）
            ffmpeg.addArgument("-s");
            ffmpeg.addArgument("1280x720");

            String fontColor = "white@0.3";  // 固定字体颜色
            int fontSize = 30;  // 固定字体大小
            int positionX = 100;  // 固定文字位置X坐标
            int positionY = 100;  // 固定文字位置Y坐标

            // 添加文字水印的过滤器，参数已写死
            String drawTextFilter = String.format(
                    "drawtext=text='%s':fontcolor=%s:fontsize=%d:x=%d:y=%d",
                    textWaterMark, fontColor, fontSize, positionX, positionY
            );
            ffmpeg.addArgument("-vf");
            ffmpeg.addArgument(drawTextFilter);

            // 输出路径（重新编码）
            ffmpeg.addArgument(targetVideoPath);

            // 执行命令
            ffmpeg.execute();

            // 检查错误输出流
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.err.println(line); // 打印 FFmpeg 的错误日志
                }
            }

            System.out.println("文字水印添加成功: " + targetVideoPath);
            return true;

        } catch (IOException e) {
            System.err.println("文字水印添加失败: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            // 释放资源
            if (ffmpeg != null) {
                ffmpeg.destroy();
            }
        }
    }

    /**
     * 给视频添加图片水印
     *
     * @param originVideoPath 原始视频路径
     * @param imagePath 水印图片
     * @param targetVideoPath 输出视频路径
     * @return 是否添加成功
     */
    public  boolean addImageWatermark(String originVideoPath, String imagePath, String targetVideoPath) {
        ProcessWrapper ffmpeg = null;
        try {
            // 创建 FFmpeg 命令执行器
            ffmpeg = new DefaultFFMPEGLocator().createExecutor();

            // 输入原始视频路径
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(originVideoPath);

            // 输入水印图片路径
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(imagePath);

            // 设置视频编码（固定为 libx264，压缩质量高，兼容性好）
            ffmpeg.addArgument("-c:v");
            ffmpeg.addArgument("libx264");

            // 设置音频编码（保持原样）
            ffmpeg.addArgument("-c:a");
            ffmpeg.addArgument("aac");

            // 固定视频分辨率（例如 1280x720）
            ffmpeg.addArgument("-s");
            ffmpeg.addArgument("1280x720");

            // 使用复杂滤镜，overlay 用来添加图片水印
            String overlayFilter = "overlay=W-w-10:H-h-10";  // 将水印放置在右下角，距离底部和右侧各 10 像素
            ffmpeg.addArgument("-filter_complex");
            ffmpeg.addArgument(overlayFilter);

            // 输出路径（重新编码）
            ffmpeg.addArgument(targetVideoPath);

            // 执行命令
            ffmpeg.execute();

            // 检查错误输出流
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.err.println(line); // 打印 FFmpeg 的错误日志
                }
            }

            System.out.println("图片水印添加成功: " + targetVideoPath);
            return true;

        } catch (IOException e) {
            System.err.println("图片水印添加失败: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            // 释放资源
            if (ffmpeg != null) {
                ffmpeg.destroy();
            }
        }
    }

    /**
     * 视频分片，按视频时间分片
     *
     * @param originVideoPath 原始视频路径
     * @param targetVideoPath 输出视频路径
     * @param timeInSeconds 按时间分片
     * @return 是否分片成功
     */
    public  boolean splitVideo(String originVideoPath, String targetVideoPath, int timeInSeconds) {
        ProcessWrapper ffmpeg = null;
        try {
            // 创建 FFmpeg 命令执行器
            ffmpeg = new DefaultFFMPEGLocator().createExecutor();

            // 输入视频文件
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(originVideoPath);

            // 设置分片时长
            ffmpeg.addArgument("-segment_time");
            ffmpeg.addArgument(String.valueOf(timeInSeconds));

            // 设置复制流（不重新编码）
            ffmpeg.addArgument("-c");
            ffmpeg.addArgument("copy");

            // 映射输入流
            ffmpeg.addArgument("-map");
            ffmpeg.addArgument("0");

            // 设置分片输出格式
            ffmpeg.addArgument("-f");
            ffmpeg.addArgument("segment");

            // 设置输出文件路径，使用数字递增的方式命名输出文件
            ffmpeg.addArgument(targetVideoPath + "%03d.mp4");

            // 执行 FFmpeg 命令
            ffmpeg.execute();

            // 检查 FFmpeg 错误输出流
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.err.println(line); // 打印 FFmpeg 错误日志，方便调试
                }
            }

            System.out.println("视频分片成功！");
            return true;

        } catch (IOException e) {
            System.err.println("视频分片失败: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            // 释放资源
            if (ffmpeg != null) {
                ffmpeg.destroy();
            }
        }
    }

    /**
     * 视频分片，按视频大小分片
     *
     * @param originVideoPath 原始视频路径
     * @param targetVideoPath 输出视频路径
     * @return 是否分片成功
     */
    public  boolean checkAndSplitVideo(String originVideoPath, String targetVideoPath) {
        File videoFile = new File(originVideoPath);

        // 检查文件是否存在
        if (!videoFile.exists()) {
            System.err.println("视频文件不存在: " + originVideoPath);
            return false;
        }

        // 获取视频文件大小（字节）
        long fileSize = videoFile.length();

        // 10MB = 10 * 1024 * 1024 字节
        long sizeLimit = 10 * 1024 * 1024;

        // 根据文件大小决定是否分割
        if (fileSize > sizeLimit) {
            // 如果文件大于 10MB，分割视频
            System.out.println("视频文件大于10MB，开始分割...");
            return splitVideoBySize(originVideoPath, targetVideoPath, sizeLimit);
        } else {
            // 如果文件小于或等于 10MB，不分割
            System.out.println("视频文件小于或等于10MB，无需分割");
            return copyVideoWithoutSplit(originVideoPath, targetVideoPath);
        }
    }

    private  boolean splitVideoBySize(String originVideoPath, String targetVideoPath, long sizeLimit) {
        ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
        try {
            // 设置分割限制为10MB
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(originVideoPath);

            // 设置输出大小为 10MB
            ffmpeg.addArgument("-fs");
            ffmpeg.addArgument(String.valueOf(sizeLimit)); // 设置为10MB

            // 设置输出文件路径
            ffmpeg.addArgument(targetVideoPath);

            // 执行命令
            ffmpeg.execute();

            // 检查错误输出流
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.err.println(line); // 打印 FFmpeg 的错误日志，方便调试
                }
            }

            System.out.println("视频分割成功: " + targetVideoPath);
            return true;

        } catch (IOException e) {
            System.err.println("视频分割失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (ffmpeg != null) {
                ffmpeg.destroy();
            }
        }
    }

    //复制视频
    private  boolean copyVideoWithoutSplit(String originVideoPath, String targetVideoPath) {
        ProcessWrapper ffmpeg = new DefaultFFMPEGLocator().createExecutor();
        try {
            // 不分割，直接复制视频
            ffmpeg.addArgument("-i");
            ffmpeg.addArgument(originVideoPath);

            // 设置复制流避免重新编码
            ffmpeg.addArgument("-c");
            ffmpeg.addArgument("copy");

            // 设置输出文件路径
            ffmpeg.addArgument(targetVideoPath);

            // 执行命令
            ffmpeg.execute();

            // 检查错误输出流
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(ffmpeg.getErrorStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    System.err.println(line); // 打印 FFmpeg 的错误日志，方便调试
                }
            }

            System.out.println("视频复制成功: " + targetVideoPath);
            return true;

        } catch (IOException e) {
            System.err.println("视频复制失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (ffmpeg != null) {
                ffmpeg.destroy();
            }
        }
    }
}

