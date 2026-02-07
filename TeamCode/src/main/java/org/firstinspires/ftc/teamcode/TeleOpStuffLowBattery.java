package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.List;

@Disabled
@TeleOp(name = "TeleOp Low Battery Edition")
    public class TeleOpStuffLowBattery extends LinearOpMode {

        private DcMotorEx frontLeft;
        private DcMotorEx frontRight;
        private DcMotorEx backLeft;
        private DcMotorEx backRight;

        private DcMotorEx intakeMotor;
        private DcMotorEx shootMotor;

        private Limelight3A limelight;
        private IMU imu;
        private LLResult llResult;
        private Pose3D botPose;

        private String robotSortOrder;

        private double botX;
        private double botY;

        private CRServo shootGate1;
        private CRServo shootGate2;
        // Init gamepad, motors + servo

        @Override
        public void runOpMode() {
            // Define all motors and servos
            frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
            frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
            backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
            backRight = hardwareMap.get(DcMotorEx.class, "backRight");

            intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
            shootMotor = hardwareMap.get(DcMotorEx.class, "shootMotor");

            limelight = hardwareMap.get(Limelight3A.class, "limelight");

            limelight.start();

            // Switch the pipeline to 0
            limelight.pipelineSwitch(0);

            // Initialize IMU with hub orientation settings
            imu = hardwareMap.get(IMU.class, "imu");
            RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
            imu.initialize(new IMU.Parameters((revHubOrientationOnRobot)));

            shootGate1 = hardwareMap.get(CRServo.class, "shootGate1");
            shootGate2 = hardwareMap.get(CRServo.class, "shootGate2");

            frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            // Variables
            double ticks = 	537.7;

            double shooterSpeed = 0;

            boolean isReversed = false;

            int whuhPos1 = frontLeft.getCurrentPosition();
            int whuhPos2 = frontRight.getCurrentPosition();
            int whuhPos3 = backLeft.getCurrentPosition();
            int whuhPos4 = backRight.getCurrentPosition();
            int intakePos = intakeMotor.getCurrentPosition();

            // Put initialization blocks here.
            frontLeft.setDirection(DcMotor.Direction.REVERSE);
            backLeft.setDirection(DcMotor.Direction.REVERSE);



            // Main loop for the motors
            waitForStart();
            while (opModeIsActive()) {

                double leftFrontPower;
                double rightFrontPower;
                double leftBackPower;
                double rightBackPower;

                // Gamepad movement code

                double drive = -gamepad1.left_stick_y;
                double strafe = gamepad1.left_stick_x;
                double turn = gamepad1.right_stick_x;
                if (isReversed) {
                    drive = gamepad1.left_stick_y;
//                    strafe = -gamepad1.left_stick_x;
//                    turn = -gamepad1.right_stick_x;
                }

                leftFrontPower = Range.clip(drive + turn + strafe, -1, 1);
                rightFrontPower = Range.clip(drive - turn - strafe, -1, 1);
                leftBackPower = Range.clip(drive + turn - strafe, -1, 1);
                rightBackPower = Range.clip(drive - turn + strafe, -1, 1);

                // Speed control buttons
                if (gamepad1.left_bumper) {
                    leftFrontPower /= 2;
                    leftBackPower /= 2;
                    rightFrontPower /= 2;
                    rightBackPower /= 2;
                }

                if (gamepad1.right_bumper) {
                    leftFrontPower *= 1.8;
                    leftBackPower *= 1.8;
                    rightFrontPower *= 1.8;
                    rightBackPower *= 1.8;
                }

                // Sets the power when gamepad
                frontLeft.setPower(leftFrontPower /= 1.8);
                frontRight.setPower(rightFrontPower /= 1.8);
                backLeft.setPower(leftBackPower /= 1.8);
                backRight.setPower(rightBackPower /= 1.8);

                telemetry.addData("FrontLeftMotor Speed", leftFrontPower);
                telemetry.addData("FrontRightMotor Speed", rightFrontPower);
                telemetry.addData("BackLeftMotor Speed", leftBackPower);
                telemetry.addData("BackRightMotor Speed", rightBackPower);
                telemetry.addData("Shooter Motor Power", shootMotor.getPower());
                telemetry.addData("Intake Motor Power", intakeMotor.getPower());
                telemetry.update();

                // Intake controls (in)
                if (gamepad2.right_trigger >= 0.5) {
                    intakeMotor.setPower(1);
                } else if (gamepad2.left_trigger >= 0.5) {
                    intakeMotor.setPower(-1);
                } else if (gamepad1.right_trigger >= 0.5) {
                    intakeMotor.setPower(1);
                } else if (gamepad1.left_trigger >= 0.5) {
                    intakeMotor.setPower(-1);
                } else {
                    intakeMotor.setPower(0);
                }
//                if (gamepad2.dpad_up) {
//                    intakeMotor.setPower(1);
//                } else {
//                    intakeMotor.setPower(0);
//                }

//                Intake controls (out)
//                if (gamepad2.left_trigger >= 0.5) {
//                        intakeMotor.setPower(-1);
//                } else {
//                    intakeMotor.setPower(0);
//                }
//                if (gamepad2.dpad_down) {
//                    intakeMotor.setPower(-1);
//                } else {
//                    intakeMotor.setPower(0);
//                }

                // Shooter controls (Controllers 1+2)
//                if (gamepad1.square) {
//                    shootMotor.setPower(1);
//                } else if (gamepad2.square) {
//                    shootMotor.setPower(1);
//                } else {
//                    shootMotor.setPower(0);
//                }

//                if (gamepad2.right_bumper) {
//                    if (shooterSpeed > 1) {
//                        shooterSpeed = 1;
//                    } else {
//                        shooterSpeed += 0.1;
//                    }
//                } else if (gamepad2.left_bumper) {
//                    if (shooterSpeed < 0.1) {
//                        shooterSpeed = 0.1;
//                    } else {
//                        shooterSpeed -= 0.1;
//                    }
//                }

                // TODO: remove this
                if (gamepad1.aWasPressed()) {
                    isReversed = !isReversed;
                }

                // limelight targeter (NYI (not yet implemented))
                // TODO: finish this control thing
//                if (gamepad2.y) {
//
//                }

                if (gamepad2.a) {
                    shootMotor.setPower(0.8);
//                    shootGate1.setPower(-1);
//                    shootGate2.setPower(1);
                } else {
                    shootMotor.setPower(0);
//                    shootGate1.setPower(0);
//                    shootGate2.setPower(0);
                }

                if (gamepad1.x) {
                    shootGate1.setPower(-1);
                    shootGate2.setPower(1);
                } else if (gamepad1.y) {
                    shootGate1.setPower(1);
                    shootGate2.setPower(-1);
                } else {
                    shootGate1.setPower(0);
                    shootGate2.setPower(0);
                }

            }
        }


    // LIMELIGHT FUNCTION CODE (shhhhh)
        /*
        Purpose: Initializes Limelight for use later and grabs latest results
        Arguments: none
        Returns: Latest Limelight result (object with all valid data received by a limelight)
     */

    public LLResult limelightInit() {

        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        limelight.updateRobotOrientation(orientation.getYaw());

        return limelight.getLatestResult();
    }

    /*
        Purpose: Checks that the current AprilTag is valid using pipeline filter
        Arguments: none
        Returns: boolean (result is valid)
        Global variables:
            - llResult: Array in llResult to check for valid AprilTag list
     */
    public boolean validAprilTag()  {
        // Check for valid Limelight result
        return llResult != null & llResult.isValid();
    }

    /*
        Purpose: Gets current bot position as a Vector2 (x,y) using MegaTag2
        Arguments: still none
        Returns: double
        Global variables:
            - botPose: Current robot position data in relation to an AprilTag
            - botX: Current robot X position in relation to estimated location on field
            - botY: Current robot Y position in relation to estimated location on field
     */
    public void getBotPos() {
        // get current botpose data from limelight
        botPose = llResult.getBotpose_MT2();
        if (botPose != null) {
            // Save bot positions for later use
            botX = botPose.getPosition().x;
            botY = botPose.getPosition().y;
        }

    }

    /*
        Purpose: Grabs telemetry of robot position and AprilTag ID, family, and position
        Arguments: still, once again, none
        Returns: "technically" nothing
        Global variables:
            - botX: Current robot X position in relation to estimated location on field relative to the AprilTag
            - botY: Current robot Y position in relation to estimated location on field relative to the AprilTag
            - llResult: Current Limelight results (see limelightInit)
            - telemetry: Global telemetry object, adds data to update on telemetry.update()
            - robotSortOrder: String object, contains current order of Artifacts
     */
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
    /*
        Purpose: Turn robot when a certain AprilTag is detected.
        Arguments: STILL. ZERO. ARGUMENTS. WHY IS THIS HERE.
        Returns: Robot telemetry (see botTelemetry(), up above), movement in real life (see turnLeft() and turnRight(), down below)
        Global variables:
            - llResult: Current Limelight data, see limelightInit() for more info
            - botTelemetry(): See above function comment for more info
            - turnLeft() & turnRight(): Turns robot, see related comment for more info
     */

    // TODO: implement this...
    public void moveOnAprilTag() {
        botTelemetry();

        List<LLResultTypes.FiducialResult> fiducialResults = llResult.getFiducialResults();
        for (LLResultTypes.FiducialResult fr : fiducialResults) {
                telemetry.addData("AprilTag Target X", fr.getTargetXDegrees());
                do {
                    turnLeft();
                } while (fr.getFiducialId() != 24);
//                do {
//                    turnRight();
//                } while (fr.getTargetXDegrees() != 20);

        }
        telemetry.update();
    }

    /*
        turnLeft() and turnRight()

        Purpose: Turn left and right. Very simple.
        Arguments: ONCE AGAIN, THERE ARE NONE. THERE NEVER WERE ANY. I MEAN IT. WHY DO I KEEP ADDING THESE???
        Returns: Movement in real life (turns robot)
        Global variables:
            - frontLeft: Top left motor on robot
            - frontRight: Top right motor on robot
            - backLeft: Bottom left motor on robot
            - backRight: Bottom right motor on robot
     */

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
