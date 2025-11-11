/* Copyright (c) 2023 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


//  https://www.perplexity.ai/search/i-want-to-use-ffmpeg-to-extrac-IK3qpcbkTEebabW5Fej_LA


package org.firstinspires.ftc.teamcode;

import com.antonkarpenko.ffmpegkit.FFmpegKit;
import com.antonkarpenko.ffmpegkit.FFmpegSession;
import com.antonkarpenko.ffmpegkit.FFprobeKit;
import com.antonkarpenko.ffmpegkit.ReturnCode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.antonkarpenko.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import com.antonkarpenko.ffmpegkit.FFmpegKit;
import com.antonkarpenko.ffmpegkit.FFmpegSession;
import com.antonkarpenko.ffmpegkit.ReturnCode;
import com.qualcomm.robotcore.util.RobotLog;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



/*
 * This OpMode illustrates using a camera to locate and drive towards a specific AprilTag.
 * The code assumes a basic two-wheel (Tank) Robot Drivetrain
 *
 * For an introduction to AprilTags, see the ftc-docs link below:
 * https://ftc-docs.firstinspires.org/en/latest/apriltag/vision_portal/apriltag_intro/apriltag-intro.html
 *
 * When an AprilTag in the TagLibrary is detected, the SDK provides location and orientation of the tag, relative to the camera.
 * This information is provided in the "ftcPose" member of the returned "detection", and is explained in the ftc-docs page linked below.
 * https://ftc-docs.firstinspires.org/apriltag-detection-values
 *
 * The driving goal is to rotate to keep the tag centered in the camera, while driving towards the tag to achieve the desired distance.
 * To reduce any motion blur (which will interrupt the detection process) the Camera exposure is reduced to a very low value (5mS)
 * You can determine the best exposure and gain values by using the ConceptAprilTagOptimizeExposure OpMode in this Samples folder.
 *
 * The code assumes a Robot Configuration with motors named left_drive and right_drive.
 * The motor directions must be set so a positive power goes forward on both wheels;
 * This sample assumes that the default AprilTag Library (usually for the current season) is being loaded by default
 * so you should choose to approach a valid tag ID.
 *
 * Under manual control, the left stick will move forward/back, and the right stick will rotate the robot.
 * This is called POV Joystick mode, different than Tank Drive (where each joystick controls a wheel).
 *
 * Manually drive the robot until it displays Target data on the Driver Station.
 * Press and hold the *Left Bumper* to enable the automatic "Drive to target" mode.
 * Release the Left Bumper to return to manual driving mode.
 *
 *  Under "Drive To Target" mode, the robot has two goals:
 *  1) Turn the robot to always keep the Tag centered on the camera frame. (Use the Target Bearing to turn the robot.)
 *  2) Drive towards the Tag to get to the desired distance.  (Use Tag Range to drive the robot forward/backward)
 *
 *  Use DESIRED_DISTANCE to set how close you want the robot to get to the target.
 * Speed and Turn sensitivity can be adjusted using the SPEED_GAIN and TURN_GAIN constants.
 *
 * Use Android Studio to Copy this Class, and Paste it into the TeamCode/src/main/java/org/firstinspires/ftc/teamcode folder.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list.
 *
 */

@TeleOp(name="TheApplethatisbad", group = "Concept")
//@Disabled
public class TheApplethatisbad extends LinearOpMode
{


    public class FrameEmojiProcessor {

        private final ExecutorService executor = Executors.newSingleThreadExecutor();

        public void processFrameAsEmojis(String videoFilename, String timestamp, int n, int m, Telemetry telemetry, Runnable onComplete) {
            executor.execute(() -> {
                try {
                    File videoFile = new File(videoFilename);
                    String dir = videoFile.getParent();
                    if (dir == null) dir = "/";
                    String outputImage = dir + "/frame.jpg";

                    telemetry.addData("Status", "Extracting frame...");
                    telemetry.update();

                    FFmpegSession session = FFmpegKit.execute(String.format(
                            "-i \"%s\" -ss %s -frames:v 1 \"%s\"", videoFilename, timestamp, outputImage));

                    if (!ReturnCode.isSuccess(session.getReturnCode())) {
                        telemetry.addData("FFmpeg", "Failed to extract frame");
                        telemetry.update();
                        if (onComplete != null) onComplete.run();
                        return;
                    }

                    telemetry.addData("Status", "Frame extracted, loading bitmap...");
                    telemetry.update();

                    int[] colors = sampleColorsFromFrame(outputImage, n, m, telemetry);
                    if (colors.length == 0) {
                        telemetry.addData("Error", "Failed to sample colors");
                        telemetry.update();
                        if (onComplete != null) onComplete.run();
                        return;
                    }

                    String[] emojis = EmojiColorMatcher.matchColorsToEmojis(colors);

                    for (int y = 0; y < m; y++) {
                        StringBuilder line = new StringBuilder();
                        for (int x = 0; x < n; x++) {
                            line.append(emojis[y * n + x]);
                        }
                        telemetry.addData("Row " + y, line.toString());
                    }
                    telemetry.update();

                    File frameFile = new File(outputImage);
                    if (frameFile.exists()) {
                        boolean deleted = frameFile.delete();
                        telemetry.addData("Cleanup", deleted ? "Deleted frame.jpg" : "Failed to delete frame.jpg");
                        telemetry.update();
                    }

                } catch (Exception e) {
                    telemetry.addData("Exception", e.toString());
                    telemetry.update();
                } finally {
                    if (onComplete != null) onComplete.run();
                }
            });
        }

        private int[] sampleColorsFromFrame(String framePath, int n, int m, Telemetry telemetry) {
            Bitmap bitmap = null;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(framePath, options);
                int reqWidth = 200;
                int reqHeight = 200;
                options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
                options.inJustDecodeBounds = false;

                bitmap = BitmapFactory.decodeFile(framePath, options);
                if (bitmap == null) {
                    telemetry.addData("Bitmap", "Failed to decode image");
                    telemetry.update();
                    return new int[0];
                }

                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                telemetry.addData("Bitmap Size", width + "x" + height);
                telemetry.update();

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
                telemetry.addData("Bitmap Exception", e.toString());
                telemetry.update();
                return new int[0];
            } finally {
                if (bitmap != null) bitmap.recycle();
            }
        }

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


    @Override public void runOpMode()
    {

        telemetry.addData(">", "Touch START to start OpMode");
        telemetry.update();
        waitForStart();

        while (opModeIsActive())
        {



            telemetry.update();
            sleep(10);
        }
    }


}
