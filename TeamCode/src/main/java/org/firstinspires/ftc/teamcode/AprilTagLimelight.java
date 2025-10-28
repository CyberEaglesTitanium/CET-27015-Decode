package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
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
        limelight.updateRobotOrientation(orientation.getYaw());
        LLResult llResult = limelight.getLatestResult();
        if (llResult != null & llResult.isValid()) {
            Pose3D botPose = llResult.getBotpose_MT2();
            // Init telemetry (Target X, Target Y, Target area)
            telemetry.addData("Tx",llResult.getTx());
            telemetry.addData("Ty", llResult.getTy());
            telemetry.addData("Ta", llResult.getTa());
            telemetry.addData("Bot pose", botPose.toString());
            telemetry.addData("Yaw", botPose.getOrientation().getYaw());
            if (botPose != null) {
                double x = botPose.getPosition().x;
                double y = botPose.getPosition().y;
                telemetry.addData("MT2 Location:", "(" + x + ", " + y + ")");
            }

            List<LLResultTypes.FiducialResult> fiducialResults = llResult.getFiducialResults();
            for (LLResultTypes.FiducialResult fr : fiducialResults) {
                telemetry.addData("Fiducial", "ID: %d, Family: %s, X: %.2f, Y: %.2f", fr.getFiducialId(), fr.getFamily(), fr.getTargetXDegrees(), fr.getTargetYDegrees());
            }
            telemetry.update();


        } else {
            // Init telemetry (no AprilTag found)
            telemetry.addData("Limelight", "No Matching Targets!");
            telemetry.update();
        }
    }
}
/*
--------------------------------------------
The below code (until next comment) is
the OpMode for checking for AprilTag ID 22.

May not work correctly, depends on pipeline.
--------------------------------------------
 */
//@TeleOp(name = "AprilTagLimelightTestID22")
//class AprilTagLimelightTag22 extends OpMode {
//    private Limelight3A limelight;
//    private IMU imu;
//
//
//    @Override
//    public void init() {
//        limelight = hardwareMap.get(Limelight3A.class, "limelight");
//        limelight.pipelineSwitch(1);
//        imu = hardwareMap.get(IMU.class, "imu");
//        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.RIGHT, RevHubOrientationOnRobot.UsbFacingDirection.UP);
//        imu.initialize(new IMU.Parameters((revHubOrientationOnRobot)));
//    }
//
//    @Override
//    public void start() {
//        limelight.start();
//    }
//// 3301
//    @Override
//    public void loop() {
//        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
//        limelight.updateRobotOrientation(orientation.getYaw());
//        LLResult llResult = limelight.getLatestResult();
//        if (llResult != null & llResult.isValid()) {
//            Pose3D botPose = llResult.getBotpose_MT2();
//            // Init telemetry (Target X, Target Y, Target area)
//            telemetry.addData("Current AprilTag ID", "22");
//            telemetry.addData("Tx",llResult.getTx());
//            telemetry.addData("Ty", llResult.getTy());
//            telemetry.addData("Ta", llResult.getTa());
//            telemetry.update();
//        } else {
//            // Init telemetry (no AprilTag found)
//            telemetry.addData("Current AprilTag ID", "22");
//            telemetry.addData("Limelight", "No Matching Targets!");
//            telemetry.update();
//        }
//    }
//}
///*
//--------------------------------------------
//The below code (until next comment) is
//the OpMode for checking for AprilTag ID 23.
//
//May not work correctly, depends on pipeline.
//--------------------------------------------
// */
//@TeleOp(name = "AprilTagLimelightTestID23")
//class AprilTagLimelightTag23 extends OpMode {
//    private Limelight3A limelight;
//    private IMU imu;
//
//
//    @Override
//    public void init() {
//        limelight = hardwareMap.get(Limelight3A.class, "limelight");
//        limelight.pipelineSwitch(2);
//        imu = hardwareMap.get(IMU.class, "imu");
//        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.RIGHT, RevHubOrientationOnRobot.UsbFacingDirection.UP);
//        imu.initialize(new IMU.Parameters((revHubOrientationOnRobot)));
//    }
//
//    @Override
//    public void start() {
//        limelight.start();
//    }
//
//    @Override
//    public void loop() {
//        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
//        limelight.updateRobotOrientation(orientation.getYaw());
//        LLResult llResult = limelight.getLatestResult();
//        if (llResult != null & llResult.isValid()) {
//            Pose3D botPose = llResult.getBotpose_MT2();
//            // Init telemetry (Target X, Target Y, Target area)
//            telemetry.addData("Current AprilTag ID", "23");
//            telemetry.addData("Tx",llResult.getTx());
//            telemetry.addData("Ty", llResult.getTy());
//            telemetry.addData("Ta", llResult.getTa());
//            telemetry.update();
//        } else {
//            // Init telemetry (no AprilTag found)
//            telemetry.addData("Current AprilTag ID", "23");
//            telemetry.addData("Limelight", "No Matching Targets!");
//            telemetry.update();
//        }
//    }
//}
