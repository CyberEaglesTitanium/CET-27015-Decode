package org.firstinspires.ftc.teamcode.limelight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.List;


/*
--------------------------------------------
The below code (until next comment) is
the OpMode for checking for AprilTag ID 21.

May not work correctly, depends on pipeline.
--------------------------------------------
 */
@Disabled
@TeleOp(name = "AprilTagLimelightTestID21")
public class AprilTagLimelight extends OpMode {
    private Limelight3A limelight;
    private IMU imu;


    @Override
    public void init() {
        // Init Limelight and IMU, also change pipeline to the correct filter one
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
        imu.initialize(new IMU.Parameters((revHubOrientationOnRobot)));
    }

    @Override
    public void start() {
        limelight.start();
    }

    @Override
    public void loop() {
        // Variables for YPR (yaw, pitch, roll), Limelight orientation, and Limelight results
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();

        // Get orientation from YPR and Limelight
        limelight.updateRobotOrientation(orientation.getYaw());

        // Gets data from Limelight for usage later
        LLResult llResult = limelight.getLatestResult();

        // Limelight loop, detects AprilTags, robot position, and other general Limelight info
        if (llResult != null & llResult.isValid()) {

            // Registers botpose using MegaTag2
            Pose3D botPose = llResult.getBotpose_MT2();

            // Init telemetry (Target X, Target Y, Target area)
            telemetry.addData("Tx", llResult.getTx());
            telemetry.addData("Ty", llResult.getTy());
            telemetry.addData("Ta", llResult.getTa());
            telemetry.addData("Bot pose", botPose.toString());
            telemetry.addData("Yaw", botPose.getOrientation().getYaw());

            // Checks location of robot using MegaTag2 (checks botpose)
            if (botPose != null) {
                // Robot position fetching
                double x = botPose.getPosition().x;
                double y = botPose.getPosition().y;

                // Register location with MT2 to telemetry
                telemetry.addData("MT2 Location:", "(" + x + ", " + y + ")");
            }

            // Grabs AprilTag results and puts them in a list
            List<LLResultTypes.FiducialResult> fiducialResults = llResult.getFiducialResults();

            // Logs AprilTag IDs, family, and position to telemetry
            for (LLResultTypes.FiducialResult fr : fiducialResults) {
                telemetry.addData("Fiducial", "ID: %d, Family: %s, X: %.2f, Y: %.2f", fr.getFiducialId(), fr.getFamily(), fr.getTargetXDegrees(), fr.getTargetYDegrees());
            }
            telemetry.update();


        } else {
            // Logs that no AprilTags matching the current filter were found to telemetry
            telemetry.addData("Limelight", "No Matching Targets!");
            telemetry.update();
        }
    }
}