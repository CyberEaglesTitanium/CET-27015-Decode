package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.CRServo;
//import com.qualcomm.robotcore.hardware.ColorRangeSensor;
//import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
//import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@TeleOp(name = "TeleOp Complete")
    public class TeleOpMain extends LinearOpMode {

        private DcMotorEx frontLeft;
        private DcMotorEx frontRight;
        private DcMotorEx backLeft;
        private DcMotorEx backRight;

        private DcMotorEx intakeMotor;
        private DcMotorEx shootMotor;
        private DcMotorEx shootMotor2;
        private DcMotorEx spindexifier;

//        private DigitalChannel limitationImitation;
        private DigitalChannel breakingBeams;

        private ColorSensor colSenseDeluxe;
        private SparkFunOTOS otos;

        private int index;
        private int redColor;
        private int greenColor;
        private int blueColor;
        private int colorSensorCount;
        private int currentPos;

        private double currentShootPower;

        private double currentShooterRPM;
        private double currentShooterTPS;

        public Limelight3A limelight;
        public IMU imu;
        public LLResult llResult;
        public Pose3D botPose;

        private SparkFunOTOS.Pose2D blueBotPos = new SparkFunOTOS.Pose2D(-63, 63, Math.toRadians(90));
        private SparkFunOTOS.Pose2D redBotPos = new SparkFunOTOS.Pose2D(-63, -63, Math.toRadians(90));

        private double baseLaunchPower;
        private double launchSpeedMod;

        private Timer timer1;
        private TimerTask flick;
        // ^^ evil evil little pieces of-

        private ElapsedTime elapsingTime = new ElapsedTime();

        private String robotSortOrder;

        private boolean kernelPanic;
        private boolean areTheIntakeAndSpindexerRunning;

        private double botX;
        private double botY;

        // TODO: rename these to reflect new purposes
        private Servo shootGate1; // Servo flicking device
        private Servo shootGate2; // Servo loading device
        // Functions for loading, shooting, flicking, and everything in between

    void redColorSensor() {
        redColor = colSenseDeluxe.red();
    }
    void greenColorSensor() {
        greenColor = colSenseDeluxe.green();
    }
    void blueColorSensor() {
        blueColor = colSenseDeluxe.blue();
    }

    void checkColor() {
        redColorSensor();
        greenColorSensor();
        blueColorSensor();
    }

    void flickNload() {
        shootGate1.setPosition(1);
        sleep(250);
        shootGate2.setPosition(-1);
        sleep(250);
        shootGate2.setPosition(0);
        shootGate1.setPosition(0);
    }

    void justFlick() {
            shootGate1.setPosition(-1);
            sleep(500);
            shootGate1.setPosition(0.5);
    }
    void justLoad() {
            shootGate2.setPosition(0);
            sleep(200);
            shootGate2.setPosition(1);
    }

        void spinIndex() {
            spindexifier.setTargetPosition(currentPos * index);
        }

        void resetSpindexEncoder() {
            spindexifier.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }

        void spinUseRight() {
            index += 1;
//            if (index == 4) {
//                index = 1;
//            }
            spinIndex();
            spindexifier.setPower(0.3);
        }

        void spinUseLeft() {
            index -= 1;
//            if (index == 0) {
//                index = 3;
//            }
            spinIndex();
            spindexifier.setPower(0.3);
        }

        void shootStart() {
            shootMotor.setPower(1);
            shootMotor2.setPower(-1);
            intakeMotor.setPower(0);
        }
        void shootStop() {
            shootMotor.setPower(0);
        }

//        void fullTeleOpShot() {
//            intakeMotor.setPower(1);
//            sleep(250);
//            intakeMotor.setPower(0);
//            spinUseRight();
//            shootStart();
//            sleep(250);
//            flickNload();
//            sleep(500);
//            shootStop();
//        }

        // also known as mass automation (also known as the results of an addiction to minecraft factory modpacks)
        void aimNShoot() {
//            shootStart();
            while (opModeIsActive() && (elapsingTime.seconds() < 0.8)) {
                spinUseLeft();
            }

            sleep(800);
            justFlick();
            sleep(400);
//            justLoad();
//            sleep(250);
        }

        void emergencyStop() {
            spindexifier.setPower(0);
            shootMotor.setPower(0);
            shootMotor2.setPower(0);
            shootGate1.setPosition(0.5);
        }

        // Sequential Subspace Spindexer Shenanigans
        void indexingIntakeSequence() {
            intakeMotor.setPower(1);
            while (colorSensorCount < 4) {
                if (greenColor > 68 && areTheIntakeAndSpindexerRunning) {
                    colorSensorCount += 1;
                    spinUseRight();
                    areTheIntakeAndSpindexerRunning = false;
                } else {
                    areTheIntakeAndSpindexerRunning = true;
                }
            }


        }

        // Sequential Shotgun Of Silly Spheres
        void shootSequence() {
//            for (colorSensorCount = colorSensorCount; colorSensorCount > 0; colorSensorCount--) {
                shootStart();
                spinUseRight();
                sleep(750);
                justFlick();
                sleep(100);
                justLoad();

//            }
        }

        // Is the color valid? IS IT?!?
        int colorValid() {
            if (greenColor == Range.clip(225, 200, 255)) {
                return 1;
            } else if (redColor == Range.clip(185, 170, 221) && blueColor == Range.clip(168, 155, 201)) {
                return 2;
            } else {
                return 0;
            }
        }
        int beamBroken() {
            if (breakingBeams.getState()) {
                return 1;
            } else {
                return 0;
        }
    }

        // Get the current RPM
        void getTheRPM() {
            currentShooterRPM = (shootMotor.getVelocity() / 28) * 60;
        }

        // Get the current Ticks Per Second
        double getTheTPS(int RPM) {
            return (RPM * 28) / 60;
        }

        // Set shooter velocity
        void setShooterVelocity(int RPM) {
            double currentVel = getTheTPS(RPM);
            shootMotor.setVelocity(currentVel);
        }

        @Override
        public void runOpMode() {
            // Define all motors and servos
            frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
            frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
            backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");
            backRight = hardwareMap.get(DcMotorEx.class, "backRight");

            intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
            shootMotor = hardwareMap.get(DcMotorEx.class, "shootMotor");
            shootMotor2 = hardwareMap.get(DcMotorEx.class, "shootMotor2");
            spindexifier = hardwareMap.get(DcMotorEx.class, "spindexifier");

            colSenseDeluxe = hardwareMap.get(ColorSensor.class, "colsense");

//            limitationImitation = hardwareMap.get(DigitalChannel.class, "limitSwitch");
//            limitationImitation.setMode(DigitalChannel.Mode.INPUT);
            breakingBeams = hardwareMap.get(DigitalChannel.class, "breakingBeams");
            breakingBeams.setMode(DigitalChannel.Mode.INPUT);

            otos = hardwareMap.get(SparkFunOTOS.class, "otos");

            limelight = hardwareMap.get(Limelight3A.class, "limelight");

//            limelight.start();

            // Switch the pipeline to 0
//            limelight.pipelineSwitch(0);

            // Initialize IMU with hub orientation settings
            imu = hardwareMap.get(IMU.class, "imu");
            RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
            imu.initialize(new IMU.Parameters((revHubOrientationOnRobot)));

            shootGate1 = hardwareMap.get(Servo.class, "shootGate1");
            shootGate2 = hardwareMap.get(Servo.class, "shootGate2");

            frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            intakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            intakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            spindexifier.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            spindexifier.setTargetPosition(752);
            spindexifier.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            // Variables

            currentShootPower = 0.3;

            double TICKS_PER_REV = 145.1;
            double TICKS_PER_REV_SHOOTER = (shootMotor.getVelocity() / 28) * 60;

            boolean isReversed = false;
            boolean intakeIsOn = false;
            boolean shooterIsOn = false;

            colorSensorCount = 0;
            currentPos = -178;


            otos.setPosition(new SparkFunOTOS.Pose2D());

//            if (spindexifier.getCurrentPosition() == (751.8 / 3)) {
//
//            }

            int whuhPos1 = frontLeft.getCurrentPosition();
            int whuhPos2 = frontRight.getCurrentPosition();
            int whuhPos3 = backLeft.getCurrentPosition();
            int whuhPos4 = backRight.getCurrentPosition();
            int intakePos = intakeMotor.getCurrentPosition();

            // Put initialization blocks here.
            frontLeft.setDirection(DcMotor.Direction.REVERSE);
            backLeft.setDirection(DcMotor.Direction.REVERSE);
//            shootMotor.setDirection(DcMotor.Direction.REVERSE);
//            shootMotor2.setDirection(DcMotor.Direction.REVERSE);
//            frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
//            backRight.setDirection(DcMotorSimple.Direction.REVERSE);

//            limelightInit();

            // Main loop for the motors
            waitForStart();
            while (opModeIsActive()) {
                getTheRPM();

                double leftFrontPower;
                double rightFrontPower;
                double leftBackPower;
                double rightBackPower;

                // Gamepad movement code

                double drive = -gamepad1.left_stick_y;
                double strafe = gamepad1.left_stick_x;
                double turn = gamepad1.right_stick_x;

                leftFrontPower = Range.clip(drive + turn + strafe, -1, 1);
                rightFrontPower = Range.clip(drive - turn - strafe, -1, 1);
                leftBackPower = Range.clip(drive + turn - strafe, -1, 1);
                rightBackPower = Range.clip(drive - turn + strafe, -1, 1);

                // haptic feedback intake motor
                
                if (intakeMotor.getPower() > 0){
                   gamepad1.rumble(0.5,0.5,1000);
                   gamepad2.rumble(0.25,0.25,1000);
                } else if (intakeMotor.getPower() < 0){
                    gamepad1.rumble(0.5,0.5,50);
                    gamepad2.rumble(0.25,0.25,50);
                }

                // Speed control buttons
                if (gamepad1.left_bumper) {
                    leftFrontPower /= 2;
                    leftBackPower /= 2;
                    rightFrontPower /= 2;
                    rightBackPower /= 2;
//                    currentShootPower /= 1.4;
                }

                if (gamepad1.right_bumper) {
                    leftFrontPower *= 1.8;
                    leftBackPower *= 1.8;
                    rightFrontPower *= 1.8;
                    rightBackPower *= 1.8;
//                    currentShootPower *= 1.7;
                }

                // Sets the power when gamepad
                frontLeft.setPower(leftFrontPower /= 1.8);
                frontRight.setPower(rightFrontPower /= 1.8);
                backLeft.setPower(leftBackPower /= 1.8);
                backRight.setPower(rightBackPower /= 1.8);

//                telemetry.addData("FrontLeftMotor Speed", leftFrontPower);
//                telemetry.addData("FrontRightMotor Speed", rightFrontPower);
//                telemetry.addData("BackLeftMotor Speed", leftBackPower);
//                telemetry.addData("BackRightMotor Speed", rightBackPower);
                telemetry.addData("Shooter Motor 1 Power", shootMotor.getPower());
                telemetry.addData("Shooter Motor 2 Power", shootMotor2.getPower());
//                telemetry.addData("Shooter Motor Speed", currentShooterRPM);
//                telemetry.addData("Shooter Motor Raw Speed", shootMotor.getVelocity());
//                telemetry.addData("Intake Motor Power", intakeMotor.getPower());
//                telemetry.addData("Intake Motor Speed", intakeMotor.getVelocity());
                telemetry.addData("Current OTOS position", otos.getPosition());
//                telemetry.addData("red", colSenseDeluxe.red());
//                telemetry.addData("green", colSenseDeluxe.green());
//                telemetry.addData("blue", colSenseDeluxe.blue());
//                telemetry.addData("Color Valid Value (purple = 1, green = 2)", colorValid());

                telemetry.addData("Spindexer Positions", spindexifier.getCurrentPosition());
                telemetry.update();



                // TODO: semi-rewrite
                // Intake controls (in)
                if (gamepad2.aWasPressed()) {
                    intakeIsOn = !intakeIsOn;
                }
                if (intakeIsOn && !isReversed) {
                    intakeMotor.setPower(1);
                } else if (isReversed && intakeIsOn) {
                    intakeMotor.setPower(-1);
                } else {
                    intakeMotor.setPower(0);
                }
                if (gamepad2.bWasPressed()) {
                    isReversed = !isReversed;
                }

                // TODO: remove this
//                if (gamepad2.aWasPressed()) {
//                    isReversed = !isReversed;
//                }

                // limelight targeter (NYI (not yet implemented))
                // TODO: finish this control thing
//                if (gamepad2.y) {
//                    botTelemetry();
//                    moveOnAprilTag();
//                }

                // stupid little flicker
                if (gamepad2.xWasPressed()) {
                    emergencyStop();
                }

                // TODO: click the stick to start shooting, also add power adjustment

//                if (gamepad1.aWasPressed()) {
//                    indexingIntakeSequence();
//                }
//                if (gamepad1.bWasPressed()) {
//                    moveOnAprilTag(20);
//                }
//                if (gamepad2.yWasPressed()) {
//                    shooterIsOn = !shooterIsOn;
//                }
//                if (gamepad2.xWasPressed()) {
//                    currentShootPower = 0.4;
//                }
//                if (gamepad2.aWasPressed()) {
//                    currentShootPower = 0.5;
//                }
//                if (gamepad2.bWasPressed()) {
//                    currentShootPower = 0.6;
//                }
//                if (gamepad2.xWasPressed()) {
//                    moveOnAprilTag(20);
//                    sleep(500);
//                    shootSequence();
//                }
                if (gamepad1.yWasPressed()) {
                    shooterIsOn = !shooterIsOn;

                }
                if (gamepad1.xWasPressed()) {
                    currentShootPower = 0.4;
                }
                if (gamepad1.aWasPressed()) {
                    currentShootPower = 0.5;
                }
                if (gamepad1.bWasPressed()) {
                    currentShootPower = 0.6;
                }
                if (gamepad1.right_trigger > 0.5) {
                    currentShootPower = 0.65;
                }
                if (gamepad1.left_trigger > 0.5) {
                    frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                    backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                } else {
                    frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                }


                if (shooterIsOn) {
                    shootMotor.setPower(currentShootPower);
                    shootMotor2.setPower(currentShootPower);
                } else {
                    shootMotor.setPower(0);
                    shootMotor2.setPower(0);
                }

                if (gamepad2.right_trigger > 0.5) {
                    justFlick();
                }
                if (gamepad2.left_trigger > 0.5) {
                    justLoad();
                }

                // it got rewritten
                if (gamepad1.dpadUpWasPressed()) {
                    if (currentShootPower < 1) {
                        currentShootPower += 0.1;
                    }
                }
                if (gamepad1.dpadDownWasPressed()) {
                    if (currentShootPower > 0) {
                        currentShootPower -= 0.1;
                    }
                }

                // FREEWHEEL SPINDEXER ACTIVATE (PANIC BUTTON)
                if (gamepad2.shareWasPressed()) {
                    kernelPanic = !kernelPanic;
                    if (kernelPanic) {
                        spindexifier.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                    } else {
                        spindexifier.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                        resetSpindexEncoder();
                    }

                }
                if (gamepad2.rightBumperWasPressed()) {
                    if (!kernelPanic) {
                        spinUseRight();
                    } else {
                        spindexifier.setPower(0.3);
                    }
                } else if (gamepad2.leftBumperWasPressed()) {
                    if (!kernelPanic) {
                        spinUseLeft();
                    } else {
                        spindexifier.setPower(-0.3);
                    }
                } else if (kernelPanic && !gamepad2.left_bumper && !gamepad2.right_bumper) {
                    spindexifier.setPower(0);
                }

            }
        }
    }
