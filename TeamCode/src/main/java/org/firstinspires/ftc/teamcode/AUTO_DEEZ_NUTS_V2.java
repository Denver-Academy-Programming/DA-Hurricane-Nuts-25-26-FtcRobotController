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
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

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

@Autonomous(name="AUTO_DEEZ_NUTS_V2", group = "Concept")
//@Disabled
public class AUTO_DEEZ_NUTS_V2 extends LinearOpMode
{
    // Adjust these numbers to suit your robot.

    //  Set the GAIN constants to control the relationship between the measured position error, and how much power is
    //  applied to the drive motors to correct the error.
    //  Drive = Error * Gain    Make these values smaller for smoother control, or larger for a more aggressive response.

    //    private static final boolean USE_WEBCAM = true;  // Set true to use a webcam, or false for a phone camera
//    private VisionPortal visionPortal;               // Used to manage the video source.



    @Override public void runOpMode()
    {

        // Initialize the Apriltag Detection process
//
        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must match the names assigned during the robot configuration.
        // step (using the FTC Robot Controller app on the phone).
        //  Used to control the left drive wheel
        DcMotor leftDrive = hardwareMap.get(DcMotor.class, "frontLeftDrive");
        //  Used to control the right drive wheel
        DcMotor rightDrive = hardwareMap.get(DcMotor.class, "frontRightDrive");
        DcMotor luancher = hardwareMap.get(DcMotor.class, "launcherMotor");
        CRServo leftLoad = hardwareMap.get(CRServo.class, "leftLoadServo");
        CRServo rightLoad = hardwareMap.get(CRServo.class, "rightLoadServo");

        // To drive forward, most robots need the motor on one side to be reversed because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Single Gear Reduction or 90 Deg drives may require direction flips
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);

        rightLoad.setDirection(CRServo.Direction.REVERSE);
        luancher.setDirection(DcMotor.Direction.REVERSE);

//        if (USE_WEBCAM)
//            setManualExposure();  // Use low exposure time to reduce motion blur

        // Wait for the driver to press Start
        telemetry.addData(">", "Touch START to start Auto");
        telemetry.update();
        waitForStart();


//        telemetry.addData("STAGE 1", "start");
//        telemetry.update();
//        luancher.setPower(1);
//        sleep(2000);
//        rightLoad.setPower(1);
//        leftLoad.setPower(1);
//        sleep(250);
//        rightLoad.setPower(0);
//        leftLoad.setPower(0);
//        sleep(500);
//        luancher.setPower(0);
//        telemetry.addData("STAGE 1", "done");
//        telemetry.update();
//        sleep(3000);
//        telemetry.addData("STAGE 2", "start");
//        telemetry.update();
//        luancher.setPower(1);
//        sleep(2000);
//        rightLoad.setPower(1);
//        leftLoad.setPower(1);
//        sleep(250);
//        rightLoad.setPower(0);
//        leftLoad.setPower(0);
//        sleep(500);
//        luancher.setPower(0);
//        telemetry.addData("STAGE 2", "done");
//        telemetry.update();
//        sleep(3000);
//        telemetry.addData("STAGE 3", "start");
//        telemetry.update();
//        luancher.setPower(1);
//        sleep(2000);
//        rightLoad.setPower(1);
//        leftLoad.setPower(1);
//        sleep(250);
//        rightLoad.setPower(0);
//        leftLoad.setPower(0);
//        sleep(500);
//        luancher.setPower(0);



        luancher.setPower(1);
        for (int i = 0; i < 4; i++) {
            telemetry.addData("STAGE ", String.valueOf(i), " start");
            telemetry.update();
            sleep(1000);
            rightLoad.setPower(1);
            leftLoad.setPower(1);
            telemetry.update();
            sleep(250);
            rightLoad.setPower(0);
            leftLoad.setPower(0);
            telemetry.addData("STAGE ", String.valueOf(i), " done");
            telemetry.update();
        }
        luancher.setPower(0);



    }

//    private void initAprilTag() {
//        // Create the AprilTag processor by using a builder.
//        // Used for managing the AprilTag detection process.
//        AprilTagProcessor aprilTag = new AprilTagProcessor.Builder()
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
