package org.firstinspires.ftc.teamcode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import com.antonkarpenko.ffmpegkit.FFmpegKit;
import com.antonkarpenko.ffmpegkit.FFmpegSession;
import com.antonkarpenko.ffmpegkit.ReturnCode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.antonkarpenko.ffmpegkit.FFmpegKit;
import com.antonkarpenko.ffmpegkit.FFmpegSession;
import com.antonkarpenko.ffmpegkit.FFprobeKit;
import com.antonkarpenko.ffmpegkit.ReturnCode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="TheApplethatisbad2", group = "Concept")
public class theApplethatisbad2 extends LinearOpMode {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // Emoji matcher static helper
    public static class EmojiColorMatcher {
        private static final String[] EMOJIS = {"⬛", "🟥", "🟧", "🟨", "🟩", "🟦", "🟪", "⬜"};
        private static final int[] COLORS = {
                Color.rgb(0, 0, 0),
                Color.rgb(255, 0, 0),
                Color.rgb(255, 165, 0),
                Color.rgb(255, 255, 0),
                Color.rgb(0, 128, 0),
                Color.rgb(0, 0, 255),
                Color.rgb(128, 0, 128),
                Color.rgb(255, 255, 255)
        };

        private static int colorDistance(int c1, int c2) {
            int dr = Color.red(c1) - Color.red(c2);
            int dg = Color.green(c1) - Color.green(c2);
            int db = Color.blue(c1) - Color.blue(c2);
            return dr * dr + dg * dg + db * db;
        }

        public static String[] matchColorsToEmojis(int[] colors) {
            String[] result = new String[colors.length];
            for (int i = 0; i < colors.length; i++) {
                int minDist = Integer.MAX_VALUE;
                int bestIdx = 0;
                for (int j = 0; j < COLORS.length; j++) {
                    int dist = colorDistance(colors[i], COLORS[j]);
                    if (dist < minDist) {
                        minDist = dist;
                        bestIdx = j;
                    }
                }
                result[i] = EMOJIS[bestIdx];
            }
            return result;
        }
    }

    // Extract frame and process on background thread
    public void extractFrameInBackground(String videoPath, String timestamp, int n, int m, Runnable onComplete) {
        executor.execute(() -> {
            try {
                File videoFile = new File(videoPath);
                String dir = videoFile.getParent();
                if (dir == null) dir = "/";
                String outputImage = dir + "/frame.jpg";

                String command = String.format("-i \"%s\" -ss %s -frames:v 1 \"%s\"", videoPath, timestamp, outputImage);
                FFmpegSession session = FFmpegKit.execute(command);

                if (!ReturnCode.isSuccess(session.getReturnCode())) {
                    telemetry.addData("FFmpeg", "Frame extraction failed");
                    telemetry.update();
                    if (onComplete != null) onComplete.run();
                    return;
                }

                // Proceed to sample and map colors
                int[] colors = sampleColorsFromFrame(outputImage, n, m);
                String[] emojis = EmojiColorMatcher.matchColorsToEmojis(colors);

                // Print emoji rows to telemetry
                for (int y = 0; y < m; y++) {
                    StringBuilder row = new StringBuilder();
                    for (int x = 0; x < n; x++) {
                        row.append(emojis[y * n + x]);
                    }
                    telemetry.addData("Row " + y, row.toString());
                }
                telemetry.update();

                // Delete frame file after processing
                File frameFile = new File(outputImage);
                if (frameFile.exists()) frameFile.delete();

            } catch (Exception e) {
                telemetry.addData("Error", e.toString());
                telemetry.update();
            } finally {
                if (onComplete != null) onComplete.run();
            }
        });
    }

    // Sample pixels evenly spaced exactly n x m from bitmap at framePath
    public int[] sampleColorsFromFrame(String framePath, int n, int m) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(framePath);
            if (bitmap == null) return new int[0];

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] colors = new int[n * m];

            float stepX = (n == 1) ? 0 : (float)(width - 1) / (n - 1);
            float stepY = (m == 1) ? 0 : (float)(height - 1) / (m - 1);

            int index = 0;
            for (int y = 0; y < m; y++) {
                int pixelY = Math.min(Math.round(y * stepY), height - 1);
                for (int x = 0; x < n; x++) {
                    int pixelX = Math.min(Math.round(x * stepX), width - 1);
                    colors[index++] = bitmap.getPixel(pixelX, pixelY);
                }
            }
            return colors;
        } catch (Exception e) {
            telemetry.addData("Bitmap Error", e.toString());
            telemetry.update();
            return new int[0];
        } finally {
            if (bitmap != null) bitmap.recycle();
        }
    }

    // Call example: inside runOpMode or loop, adjusting paths and n, m accordingly
    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Starting");
        telemetry.update();

        waitForStart();

        extractFrameInBackground("badapple.mp4", "00:00:03.000", 10, 10, () -> {
            telemetry.addData("Status", "Processing done");
            telemetry.update();
        });

        while (opModeIsActive()) {
            // Your regular loop code
            sleep(100);
        }
    }
}
