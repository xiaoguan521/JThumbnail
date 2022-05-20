package io.github.makbn.thumbnailer;

import io.github.makbn.thumbnailer.listener.ThumbnailListener;
import io.github.makbn.thumbnailer.model.ThumbnailCandidate;

import java.io.File;
import java.io.IOException;

/**
 * Created by Mehdi Akbarian-Rastaghi on 9/30/18
 */

public class Test {

    public static void main(String[] args) {

        try {
            AppSettings.init(args);
            Thumbnailer.start();
            File in = new File("C:\\Users\\xiaochen\\Desktop\\123.docx");
            if(in.exists()) {
                ThumbnailCandidate candidate = new ThumbnailCandidate(in,"unique_code");

                Thumbnailer.createThumbnail(candidate, new ThumbnailListener() {
                    @Override
                    public void onThumbnailReady(String hash, File thumbnail) {
                        System.out.println("FILE created at : " + thumbnail.getAbsolutePath());
                    }

                    @Override
                    public void onThumbnailFailed(String hash, String message, int code) {
                        System.out.println(message);
                    }
                });
            }else{
                System.out.println("没有找到文件" +
                        "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
