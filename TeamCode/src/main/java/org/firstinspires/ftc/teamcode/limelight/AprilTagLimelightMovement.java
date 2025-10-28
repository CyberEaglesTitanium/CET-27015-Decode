package org.firstinspires.ftc.teamcode.limelight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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
@Autonomous(name = "AprilTag Alignment Movement OpMode Thing")
public class AprilTagLimelightMovement extends OpMode {
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
        limelight.updateRobotOrientation(orientation.getYaw());
        LLResult llResult = limelight.getLatestResult();
        if (llResult != null & llResult.isValid()) {
            Pose3D botPose = llResult.getBotpose_MT2();
            if (botPose != null) {
                double x = botPose.getPosition().x;
                double y = botPose.getPosition().y;
                telemetry.addData("MT2 Location:", "(" + x + ", " + y + ")");
            }

            List<LLResultTypes.FiducialResult> fiducialResults = llResult.getFiducialResults();
            for (LLResultTypes.FiducialResult fr : fiducialResults) {
                if (fr.getFiducialId() == 20) {
                    
                }
                telemetry.addData("Fiducial", "ID: %d, Family: %s, X: %.2f, Y: %.2f", fr.getFiducialId(), fr.getFamily(), fr.getTargetXDegrees(), fr.getTargetYDegrees());
            }
            telemetry.update();
        }
    }
}