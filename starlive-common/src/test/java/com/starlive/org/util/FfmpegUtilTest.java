package com.starlive.org.util;

import org.junit.jupiter.api.Test;
import ws.schild.jave.EncoderException;
import ws.schild.jave.info.MultimediaInfo;

public class FfmpegUtilTest {
    FfmpegUtil ffmpegUtil = new FfmpegUtil();
    @Test
    public void test() {
        ffmpegUtil.formatAudioLocal("C:\\Users\\EvoltoStar\\Videos\\fc6eddf6864e70886ade66ceb58aed56.mp4","C:\\Users\\EvoltoStar\\Videos\\xxx.wav");
    }
    @Test
    public void test2() {
        ffmpegUtil.getTargetThumbnail("C:\\Users\\EvoltoStar\\Videos\\fc6eddf6864e70886ade66ceb58aed56.mp4","C:\\Users\\EvoltoStar\\Videos\\xx.jpg","5");
    }
    @Test
    public void test3() {
        ffmpegUtil.addSubtitle("C:\\Users\\EvoltoStar\\Videos\\xxx.mp4","C:\\Users\\EvoltoStar\\Videos\\xxx.srt","C:\\Users\\EvoltoStar\\Videos\\fc6eddf6864vve6.mp4");
    }
    @Test
    public void test4() {
        MultimediaInfo multimediaInfo = ffmpegUtil.getMultimediaInfo("C:\\Users\\EvoltoStar\\Videos\\fc6eddf6864e70886ade66ceb58aed56.mp4");
        System.out.println(multimediaInfo);
    }
    @Test
    public void test5() {
        ffmpegUtil.formatToMp4Local("C:\\Users\\EvoltoStar\\Videos\\fhh.mp4","C:\\Users\\EvoltoStar\\Videos\\xxx.avi","avi");
    }
    @Test
    public void test7() throws EncoderException {
        ffmpegUtil.addTextWatermark("C:\\Users\\EvoltoStar\\Videos\\fc6eddf6864vve6.mp4","xxxxjjd","C:\\Users\\EvoltoStar\\Videos\\fhh.mp4");
    }
    @Test
    public void test8() {
        ffmpegUtil.addImageWatermark("C:\\Users\\EvoltoStar\\Videos\\fc6eddf6864vve6.mp4","C:\\Users\\EvoltoStar\\Pictures\\Screenshots\\屏幕截图 2024-10-12 000746.png","C:\\Users\\EvoltoStar\\Videos\\vvv.mp4");
    }

    @Test
    public void test10() {
        ffmpegUtil.checkAndSplitVideo("C:\\Users\\EvoltoStar\\Videos\\fc6eddf6864vve6.mp4","C:\\Users\\EvoltoStar\\Videos\\xve.mp4");
    }
}
