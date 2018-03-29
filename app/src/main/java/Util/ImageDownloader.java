package Util;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import GameGrabber.Game;
import GameGrabber.GameLab;

/**
 * Created by Dounx on 2018/3/30.
 */

public class ImageDownloader {
    public void ImagesDownload(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Game> games = GameLab.get(context).getGames();

                File dir = new File("data/data/" + context.getPackageName() + "/images");

                if (!dir.exists() || !dir.isDirectory()) {
                    dir.mkdir();
                }
                Log.d("Download Images", "Total: " + games.size());
                for (Game game : games) {
                    if (game.getIconUrl() == null || game.getGameCode() == null || game.getGameCode().equals("AB38")) {
                        continue;
                    }
                    try {
                        File file = null;
                        while (file == null) {
                            Log.d("Download Images", "Start: " + game.getTitle());
                            file = Glide.with(context)
                                    .load(game.getIconUrl())
                                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                    .get();
                        }

                        File imageFile = new File(dir, game.getGameCode() + ".jpg");

                        InputStream inputStream = null;
                        OutputStream outputStream = null;

                        if (!imageFile.exists()) {
                            try {
                                inputStream = new FileInputStream(file);
                                outputStream = new FileOutputStream(imageFile);

                                byte[] buffer = new byte[4096];
                                int len;

                                while ((len = inputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, len);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (outputStream != null) {
                                    outputStream.flush();
                                    outputStream.close();
                                }
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                                Log.d("Download Images", "Success: " + game.getTitle());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.d("Download Images", "Success!");
            }
        }).start();
    }
}
