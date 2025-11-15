package org.firstinspires.ftc.teamcode;

import com.antonkarpenko.ffmpegkit.FFmpegKit;
import com.antonkarpenko.ffmpegkit.FFmpegSession;
import com.antonkarpenko.ffmpegkit.ReturnCode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import android.graphics.Bitmap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TeleOp(name = "TheApplethatisbad", group = "Concept")
public class TheApplethatisbad extends LinearOpMode {

    // Stores the emoji output and debug info so it's always shown
    private volatile String emojiOutput = "";

    @Override
    public void runOpMode() {
        telemetry.addData(">", "Touch START to start OpMode");
        telemetry.addData("MEIOAHOOFoiujkbhfukjdsmgyhfuz", "🟥🟧🟨🟩🟦🟪⬜");

        URL path = TheApplethatisbad.class.getResource("badapple.mp4");
        telemetry.addData("path", path);
        assert path != null;
        String video = path.getFile();
        telemetry.addData("Video", video);
        telemetry.update();
        waitForStart();
        String timestamp = "00:00:03.000";
        int n = 10;
        int m = 10;

        emojiOutput = "Processing...\n"; // Initial message

        // Begin processing asynchronously, store all telemetry in emojiOutput
        new FrameEmojiProcessor().processFrameAsEmojis(video, timestamp, n, m, (output) -> {
            emojiOutput = output;
        });

        // Main loop: always show the latest emojiOutput
        while (opModeIsActive()) {
            telemetry.addLine(emojiOutput);
            telemetry.update();
            sleep(100);
        }
    }
}

class FrameEmojiProcessor {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // The callback accepts a string with the full grid and all debug/status info
    public void processFrameAsEmojis(String videoFilename, String timestamp, int n, int m, java.util.function.Consumer<String> outputCb) {
        executor.execute(() -> {
            StringBuilder debugOutput = new StringBuilder();
            try {
                debugOutput.append("Starting processFrameAsEmojis\n");
                File videoFile = new File(videoFilename);
                String dir = videoFile.getParent();
                if (dir == null) dir = ".";
                String outputImage = dir + "/frame.jpg";

                debugOutput.append("Video file: ").append(videoFilename).append('\n');
                debugOutput.append("Checking file...\n");
                if (!videoFile.exists()) {
                    debugOutput.append("Error: Video file does not exist!\n");
                    outputCb.accept(debugOutput.toString());
                    return;
                }
                debugOutput.append("Video file exists\n");

                debugOutput.append("Extracting frame...\n");

                FFmpegSession session = FFmpegKit.execute(String.format(
                        "-i \"%s\" -ss %s -frames:v 1 \"%s\"", videoFilename, timestamp, outputImage));

                if (session == null) {
                    debugOutput.append("FFmpegKit session null!\n");
                    outputCb.accept(debugOutput.toString());
                    return;
                }
                debugOutput.append("FFmpegKit session done, returncode=").append(session.getReturnCode()).append('\n');
                debugOutput.append("Session Output: ").append(session.getOutput()).append('\n');

                if (!ReturnCode.isSuccess(session.getReturnCode())) {
                    debugOutput.append("FFmpeg error: failed to extract frame!\n");
                    outputCb.accept(debugOutput.toString());
                    return;
                }

                File frameFile = new File(outputImage);
                debugOutput.append("Output image: ").append(outputImage).append(" (Size: ").append(frameFile.length()).append(")\n");

                if (!frameFile.exists() || frameFile.length() < 100) {
                    debugOutput.append("Error: Frame file missing or too small!\n");
                    outputCb.accept(debugOutput.toString());
                    return;
                }

                debugOutput.append("Loading bitmap...\n");
                int[] colors = sampleColorsFromFrame(outputImage, n, m, debugOutput);
                debugOutput.append("Colors sampled: ").append(colors.length).append('\n');

                if (colors.length == 0) {
                    debugOutput.append("Error: Failed to sample colors.\n");
                    outputCb.accept(debugOutput.toString());
                    return;
                }

                String[] emojis = EmojiColorMatcher.matchColorsToEmojis(colors);

                debugOutput.append("Emoji Grid:\n");
                for (int y = 0; y < m; y++) {
                    for (int x = 0; x < n; x++) {
                        debugOutput.append(emojis[y * n + x]);
                    }
                    debugOutput.append("\n");
                }

                if (frameFile.exists()) {
                    boolean deleted = frameFile.delete();
                    debugOutput.append("Cleanup: ").append(deleted ? "Deleted" : "FAILED TO DELETE").append(" frame.jpg\n");
                }

            } catch (Exception e) {
                debugOutput.append("Exception: ").append(e.toString()).append('\n');
            }
            outputCb.accept(debugOutput.toString());
        });
    }

    // Returns colors, logs all actions to debugOutput
    private int[] sampleColorsFromFrame(String framePath, int n, int m, StringBuilder debugOutput) {
        Bitmap bitmap = null;
        try {
            debugOutput.append("sampleColorsFromFrame: ").append(framePath).append('\n');

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(framePath, options);

            debugOutput.append("Original image size: ").append(options.outWidth).append(" x ").append(options.outHeight).append('\n');

            int reqWidth = 200;
            int reqHeight = 200;
            options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;

            bitmap = BitmapFactory.decodeFile(framePath, options);

            if (bitmap == null) {
                debugOutput.append("Bitmap decodeFile returned null!\n");
                return new int[0];
            }
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            debugOutput.append("Decoded Bitmap: ").append(width).append(" x ").append(height).append('\n');

            int[] colors = new int[n * m];
            float stepX = (n == 1) ? 0 : (float) (width - 1) / (n - 1);
            float stepY = (m == 1) ? 0 : (float) (height - 1) / (m - 1);

            int index = 0;
            for (int y = 0; y < m; y++) {
                int pixelY = Math.min(Math.round(y * stepY), height - 1);
                for (int x = 0; x < n; x++) {
                    int pixelX = Math.min(Math.round(x * stepX), width - 1);
                    colors[index++] = bitmap.getPixel(pixelX, pixelY);
                }
            }
            debugOutput.append("Successfully sampled ").append(index).append(" colors.\n");
            return colors;

        } catch (Exception e) {
            debugOutput.append("Bitmap Exception: ").append(e.toString()).append('\n');
            return new int[0];
        } finally {
            if (bitmap != null) bitmap.recycle();
        }
    }

    // Same as before
    private int calculateInSampleSize(int origWidth, int origHeight, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (origHeight > reqHeight || origWidth > reqWidth) {
            final int halfHeight = origHeight / 2;
            final int halfWidth = origWidth / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static class EmojiColorMatcher {
        private static final String[] EMOJIS = {"⬛", "🟥", "🟧", "🟨", "🟩", "🟦", "🟪", "⬜"};
        private static final int[] COLORS = {
                Color.rgb(0, 0, 0), Color.rgb(255, 0, 0), Color.rgb(255, 165, 0), Color.rgb(255, 255, 0),
                Color.rgb(0, 128, 0), Color.rgb(0, 0, 255), Color.rgb(128, 0, 128), Color.rgb(255, 255, 255)
        };

        private static int colorDistance(int c1, int c2) {
            int dr = Color.red(c1) - Color.red(c2);
            int dg = Color.green(c1) - Color.green(c2);
            int db = Color.blue(c1) - Color.blue(c2);
            return dr * dr + dg * dg + db * db;
        }

        public static String[] matchColorsToEmojis(int[] pixelColors) {
            String[] matched = new String[pixelColors.length];
            for (int i = 0; i < pixelColors.length; i++) {
                int minDist = Integer.MAX_VALUE, bestIdx = 0;
                for (int j = 0; j < COLORS.length; j++) {
                    int dist = colorDistance(pixelColors[i], COLORS[j]);
                    if (dist < minDist) {
                        minDist = dist;
                        bestIdx = j;
                    }
                }
                matched[i] = EMOJIS[bestIdx];
            }
            return matched;
        }
    }
}
