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

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;


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

@TeleOp(name="OPMODE_DEEZ_NUTS_V2", group = "Concept")
//@Disabled
public class OPMODE_DEEZ_NUTS_V2 extends LinearOpMode
{
    // Adjust these numbers to suit your robot.
//    final double DESIRED_DISTANCE = 12.0; //  this is how close the camera should get to the target (inches)

    //  Set the GAIN constants to control the relationship between the measured position error, and how much power is
    //  applied to the drive motors to correct the error.
    //  Drive = Error * Gain    Make these values smaller for smoother control, or larger for a more aggressive response.
//    final double SPEED_GAIN =   0.01 ;   //  Speed Control "Gain". e.g. Ramp up to 50% power at a 25 inch error.   (0.50 / 25.0)
//    final double TURN_GAIN  =   0.01 ;   //  Turn Control "Gain".  e.g. Ramp up to 25% power at a 25 degree error. (0.25 / 25.0)

//    final double MAX_AUTO_SPEED = 0.50;   //  Clip the approach speed to this max value (adjust for your robot)
//    final double MAX_AUTO_TURN  = 0.25;  //  Clip the turn speed to this max value (adjust for your robot)

    private DcMotor leftDrive   = null;  //  Used to control the left drive wheel
    private DcMotor rightDrive  = null;  //  Used to control the right drive wheel
    public int counter1 = -1;


//    private static final boolean USE_WEBCAM = true;  // Set true to use a webcam, or false for a phone camera
//    private VisionPortal visionPortal;               // Used to manage the video source.
//    private AprilTagProcessor aprilTag;              // Used for managing the AprilTag detection process.

//    public static final String Pattern = "Pattern";
//    public static final String alince = "alince";


//    public boolean edited;


    @Override public void runOpMode()
    {
//        boolean targetFound;    // Set to true when an AprilTag target is detected
        double  drive;        // Desired forward power/speed (-1 to +1) +ve is forward
        double  turn;        // Desired turning power/speed (-1 to +1) +ve is CounterClockwise




        // Initialize the Apriltag Detection process
//        initAprilTag();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must match the names assigned during the robot configuration.
        // step (using the FTC Robot Controller app on the phone).
        leftDrive  = hardwareMap.get(DcMotorImplEx.class, "frontLeftDrive");
        rightDrive = hardwareMap.get(DcMotorImplEx.class, "frontRightDrive");
        DcMotorImplEx luancher = hardwareMap.get(DcMotorImplEx.class, "launcherMotor");
        CRServo leftLoad = hardwareMap.get(CRServo.class, "leftLoadServo");
        CRServo rightLoad = hardwareMap.get(CRServo.class, "rightLoadServo");

        // To drive forward, most robots need the motor on one side to be reversed because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Single Gear Reduction or 90 Deg drives may require direction flips
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);

        rightLoad.setDirection(CRServo.Direction.REVERSE);
        luancher.setDirection(DcMotor.Direction.REVERSE);

        luancher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

//        if (USE_WEBCAM)
//            setManualExposure();  // Use low exposure time to reduce motion blur

        // Wait for the driver to press Start
//        telemetry.addData("Camera preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch START to start OpMode");
        telemetry.addData("MEIOAHOOFoiujkbhfukjdsmgyhfuz", "🟥🟧🟨🟩🟦🟪⬜");
        telemetry.update();
        waitForStart();

        while (opModeIsActive())
        {
//            blackboard.putIfAbsent(Pattern, 21);
//            Object colorcode;
//            if ((int) blackboard.getOrDefault(Pattern, -1) == 21) {
//                colorcode = "GPP";
//            } else if ((int) blackboard.getOrDefault(Pattern,  -1) == 22) {
//                colorcode = "PGP";
//            } else if ((int) blackboard.getOrDefault(Pattern, -1) == 23) {
//                colorcode = "PPG";
//            } else {
//                colorcode = "E_67";
//                telemetry.addData("e", blackboard.getOrDefault(Pattern, -1));
//            }

//            targetFound = false;
//            // Used to hold the data for a detected AprilTag
//            AprilTagDetection desiredTag = null;
//            // Step through the list of detected tags and look for a matching tag
//            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
//            for (AprilTagDetection detection : currentDetections) {
//                // Look to see if we have size info on this tag.
//                if (detection.metadata != null) {
//                    //  Check to see if we want to track towards this tag.
//                    // Choose the tag you want to approach or set to -1 for ANY tag.
//                    int DESIRED_TAG_ID = (int) blackboard.getOrDefault(alince, -1);
//                    if ((DESIRED_TAG_ID < 0) || (detection.id == DESIRED_TAG_ID) || (detection.id == 21 || detection.id == 22 || detection.id == 23)) {
//                        // Yes, we want to use this tag.
//                        if ((detection.id == 21) || (detection.id == 22) || (detection.id == 23)) {
//
//                            if (!edited) {
//                                blackboard.put(Pattern, detection.id);
//                            }
//                            telemetry.addData("Pattern blackboard id - live", detection.id);
//                        } else {
//                            targetFound = true;
//                            desiredTag = detection;
//                            break;  // don't look any further.
//                        }
//                    } else {
//                        // This tag is in the library, but we do not want to track it right now.
//                        telemetry.addData("Skipping", "Tag ID %d is not desired", detection.id);
//                    }
//                } else {
//
//                    // This tag is NOT in the library, so we don't have enough information to track to it.
//                    telemetry.addData("Unknown", "Tag ID %d is not in TagLibrary", detection.id);
//                }
//            }
//
//            // Tell the driver what we see, and what to do.
//            if (targetFound) {
//                telemetry.addData("\n>","HOLD Left-Bumper to Drive to Target\n");
//                telemetry.addData("Found", "ID %d (%s)", desiredTag.id, desiredTag.metadata.name);
//                telemetry.addData("cur Range",  "%5.1f inches", desiredTag.ftcPose.range);
//                telemetry.addData("Range - distance",  "%5.1f inches", desiredTag.ftcPose.range - 12f);
//                telemetry.addData("cur Bearing","%3.0f degrees", desiredTag.ftcPose.bearing);
//                telemetry.addData("Bearing - 25","%3.0f degrees", desiredTag.ftcPose.bearing - 25f);
//                double  rangeError   = (desiredTag.ftcPose.range - DESIRED_DISTANCE);
//                double  headingError = desiredTag.ftcPose.bearing - 25f;
//
//                // Use the speed and turn "gains" to calculate how we want the robot to move.  Clip it to the maximum
//                drive = Range.clip(rangeError * SPEED_GAIN * 10, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
//                turn  = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN) ;
//
//                telemetry.addData("Autonot","Drive %5.2f, Turn %5.2f", drive, turn);
//            } else {
//                telemetry.addData("\n>","Drive using joysticks to find valid target\n");
//            }
//
//            // If Left Bumper is being pressed, AND we have found the desired target, Drive to target Automatically .
//            if (gamepad1.left_bumper && targetFound) {
//
//                // Determine heading and range error so we can use them to control the robot automatically.
//                double  rangeError   = (desiredTag.ftcPose.range - DESIRED_DISTANCE);
//                double  headingError = desiredTag.ftcPose.bearing - 25f;
//
//                // Use the speed and turn "gains" to calculate how we want the robot to move.  Clip it to the maximum
//                drive = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
//                turn  = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN) ;
//
//                telemetry.addData("Auto","Drive %5.2f, Turn %5.2f", drive, turn);
//            } else {

                // drive using manual POV Joystick mode.
                drive = -gamepad1.left_stick_y / 2.0;  // Reduce drive rate to 50%.
                turn  = -gamepad1.right_stick_x / 2.0;  // Reduce turn rate to 50%.
                telemetry.addData("Manual stick","Drive %5.2f, Turn %5.2f", -gamepad1.left_stick_y, -gamepad1.right_stick_x);
                telemetry.addData("Manual","Drive %5.2f, Turn %5.2f", drive, turn);
//            }

//            if (gamepad1.right_bumper) {
//                if (blackboard.get(Pattern) == null) {
//                    return;
//                }
//                int ptemp = (int) blackboard.get(Pattern);
//                if (ptemp + 1 < 24) {
//                    ptemp++;
//                } else {
//                    ptemp = 21;
//                }
//                blackboard.put(Pattern, ptemp);
//                edited = true;
//                sleep(250);
//            }

            //Trigger launching
            if (gamepad1.dpad_down) {
                luancher.setPower(1);
                telemetry.addData("trigger left power", "nul");
            } else {
                luancher.setPower(map(gamepad1.left_trigger, 0, 1, 0, 0.35));
                telemetry.addData("trigger left power", map(gamepad1.left_trigger, 0, 1, 0, 0.4));
            }
            telemetry.addData("launcher power", luancher.getPower());
            telemetry.addData("RPM: ", calculateRpmManual(luancher));

            //B for load
            counter1 = counter1 + 1;
            telemetry.addData("counter ##: ", counter1);
            if (gamepad1.b && (counter1 >= 100)) {
                leftLoad.setPower(1);
                rightLoad.setPower(1);
                sleep(250);
                counter1 = 0;
            } else {
                leftLoad.setPower(0);
                rightLoad.setPower(0);
            }

//            telemetry.addData("Pattern blackboard id", blackboard.get(Pattern));
//            telemetry.addData("Current Pattern code: ", colorcode);
            telemetry.update();

            // Apply desired axes motions to the drivetrain.
            moveRobot(drive, turn);
            sleep(10);
        }
    }

    /**
     * Move robot according to desired axes motions
     * <p>
     * Positive X is forward
     * <p>
     * Positive Yaw is counter-clockwise
     */
    public void moveRobot(double x, double yaw) {
        // Calculate left and right wheel powers.
        double leftPower    = x - yaw;
        double rightPower   = x + yaw;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(leftPower), Math.abs(rightPower));
        if (max >1.0) {
            leftPower /= max;
            rightPower /= max;
        }

        // Send powers to the wheels.
        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);
    }
    public static double map(double inputValue, double inputStart, double inputEnd, double outputStart, double outputEnd) {

        // Handle potential division by zero if inputStart and inputEnd are the same
        if (inputStart == inputEnd) {
            // If the input range is a single point, the output is also a single point (outputStart)
            return outputStart;
        }
        return outputStart + (outputEnd - outputStart) * ((inputValue - inputStart) / (inputEnd - inputStart));
    }


    public double calculateRpmManual(DcMotorImplEx motor) {
        double tick_per_rev = 560;
        double ticksPerSecond = motor.getVelocity();
        double revolutionsPerSecond = ticksPerSecond / tick_per_rev;
        double RPM = revolutionsPerSecond * 60;
        telemetry.addData("ticksPerSecond", ticksPerSecond);
        telemetry.addData("revolutionsPerSecond", revolutionsPerSecond);
        telemetry.addData("RPMggg", RPM);

        return RPM;
    }


    //    private void initAprilTag() {
//        // Create the AprilTag processor by using a builder.
//        aprilTag = new AprilTagProcessor.Builder()
//                .setLensIntrinsics(500.53, 500.53, 481.943, 283.426)
//
//                .build();
//
//        // Adjust Image Decimation to trade-off detection-range for detection-rate.
//        // e.g. Some typical detection data using a Logitech C920 WebCam
//        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
//        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
//        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
//        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
//        // Note: Decimation can be changed on-the-fly to adapt during a match.
//        aprilTag.setDecimation(2);
//
//        // Create the vision portal by using a builder.
//        if (USE_WEBCAM) {
//            visionPortal = new VisionPortal.Builder()
//                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
//                    .addProcessor(aprilTag)
//                    .build();
//        } else {
//            visionPortal = new VisionPortal.Builder()
//                    .setCamera(BuiltinCameraDirection.BACK)
//                    .addProcessor(aprilTag)
//                    .build();
//        }
//    }
//
//    /*
//     Manually set the camera gain and exposure.
//     This can only be called AFTER calling initAprilTag(), and only works for Webcams;
//    */
//    private void    setManualExposure() {
//        // Wait for the camera to be open, then use the controls
//
//        if (visionPortal == null) {
//            return;
//        }
//
//        // Make sure camera is streaming before we try to set the exposure controls
//        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
//            telemetry.addData("Camera", "Waiting");
//            telemetry.update();
//            while (!isStopRequested() && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
//                sleep(20);
//            }
//            telemetry.addData("Camera", "Ready");
//            telemetry.update();
//        }
//
//        // Set camera controls unless we are stopping.
//        if (!isStopRequested())
//        {
//            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
//            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
//                exposureControl.setMode(ExposureControl.Mode.Manual);
//                sleep(50);
//            }
//            exposureControl.setExposure(6, TimeUnit.MILLISECONDS);
//            sleep(20);
//            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
//            gainControl.setGain(250);
//            sleep(20);
//            telemetry.addData("Camera", "Ready");
//            telemetry.update();
//        }
//    }
}
