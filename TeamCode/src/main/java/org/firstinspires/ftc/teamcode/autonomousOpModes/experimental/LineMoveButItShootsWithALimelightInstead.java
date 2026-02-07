package org.firstinspires.ftc.teamcode.autonomousOpModes.experimental;


import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;

@Disabled
@Autonomous (name = "Extremely Basic Autonomous but Limelight")
public class LineMoveButItShootsWithALimelightInstead extends LinearOpMode {
    private Limelight3A limelight;
    private IMU imu;
    private LLResult llResult;
    private Pose3D botPose;

    private DcMotorEx frontLeft;
    private DcMotorEx frontRight;
    private DcMotorEx backLeft;
    private DcMotorEx backRight;

    private String robotSortOrder;

    private double botX;
    private double botY;

    private int aprilTagId;

    private DcMotorEx shootMotor;

    private CRServo shootGate1;
    private CRServo shootGate2;

    void launchCodes() {
        shootMotor.setPower(1);
        sleep(1500);
        shootGate1.setPower(-1);
        shootGate2.setPower(1);
        sleep(1500);
        shootMotor.setPower(0);
        shootGate1.setPower(0);
        shootGate2.setPower(0);
//        sleep(250);
//        shootGate1.setPower(1);
//        shootGate2.setPower(1);
//        sleep(100);
//        shootMotor.setPower(1);
//        sleep(600);
//        shootMotor.setPower(0);
//        sleep(250);
//        shootGate1.setPower(1);
//        shootGate2.setPower(1);
//        sleep(100);
//        shootMotor.setPower(1);
//        sleep(600);
//        shootMotor.setPower(0);
    }

    void turnDangIt() {
        frontLeft.setPower(-0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(-0.5);
        backRight.setPower(0.5);
        sleep(800);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    void shootAtTag() {
        moveOnAprilTag(24);
        sleep(300);
        launchCodes();
    }
    void shootAtTagOtherWay() {
        moveOnAprilTagOtherWay(20);
        sleep(300);
        launchCodes();
    }

    void testThing() {
        frontLeft.setPower(0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(0.5);
        backRight.setPower(0.5);
        sleep(1400);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        sleep(250);
        turnRight();
        sleep(800);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        shootAtTag();
    }

    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");

        shootMotor = hardwareMap.get(DcMotorEx.class, "shootMotor");
        shootGate1 = hardwareMap.get(CRServo.class, "shootGate1");
        shootGate2 = hardwareMap.get(CRServo.class, "shootGate2");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        aprilTagId = 24;

        waitForStart();

        testThing();
    }

    public void botTelemetry () {
        // Get direct limelight target info
        telemetry.addData("Target X", llResult.getTx());
        telemetry.addData("Target Y", llResult.getTy());
        telemetry.addData("Target Area", llResult.getTa());
        telemetry.addData("Botpose", botPose.toString());

        // Get robot location using MegaTag 2 and bot positions
        telemetry.addData("MT2 Location:", "(" + botX + ", " + botY + ")");

        // Get AprilTag IDs, positions, and family
        List<LLResultTypes.FiducialResult> fiducialResults = llResult.getFiducialResults();
        for (LLResultTypes.FiducialResult fr : fiducialResults) {
            telemetry.addData("Fiducial", "ID: %d, Family: %s, X: %.2f, Y: %.2f", fr.getFiducialId(), fr.getFamily(), fr.getTargetXDegrees(), fr.getTargetYDegrees());
            if (fr.getFiducialId() == 21) {
                robotSortOrder = "greenpurplepurple";
            } else if (fr.getFiducialId() == 22) {
                robotSortOrder = "purplegreenpurple";
            } else if (fr.getFiducialId() == 23) {
                robotSortOrder = "purplepurplegreen";
            }
            telemetry.addData("Sort Order", robotSortOrder);
        }
    }

    public void moveOnAprilTag(int AprilTag) {
        botTelemetry();

        List<LLResultTypes.FiducialResult> fiducialResults = llResult.getFiducialResults();
        for (LLResultTypes.FiducialResult fr : fiducialResults) {
            telemetry.addData("AprilTag Target X", fr.getTargetXDegrees());
            do {
                turnLeft();
            } while (fr.getFiducialId() != AprilTag);
            do {
                turnLeft();
            } while (fr.getTargetXDegrees() != 0.3);

        }
        telemetry.update();
    }

    public void moveOnAprilTagOtherWay(int AprilTag) {
        botTelemetry();

        List<LLResultTypes.FiducialResult> fiducialResults = llResult.getFiducialResults();
        for (LLResultTypes.FiducialResult fr : fiducialResults) {
            telemetry.addData("AprilTag Target X", fr.getTargetXDegrees());
            do {
                turnRight();
            } while (fr.getFiducialId() != AprilTag);
            do {
                turnRight();
            } while (fr.getTargetXDegrees() != -0.3);

        }
        telemetry.update();
    }
    public void turnLeft(){
        frontLeft.setPower(0.5);
        frontRight.setPower(-0.5);
        backLeft.setPower(0.5);
        backRight.setPower(-0.5);
    }

    public void turnRight(){
        frontLeft.setPower(-0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(-0.5);
        backRight.setPower(0.5);
    }
}
