package org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto.logic.ShooterLogic;
import org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto.logic.SpindexAutoLogic;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name = "Intake Test II", group = "Autonomous")
public class IntakeTest2 extends OpMode {
    private Follower follower;
    private Timer pathTimer, opModeTimer;

    private int index = 3;

    private DcMotorEx spindexer;
    private int BASE_INDEXER_POS = 178;

    private DcMotorEx shootMotor;
    private DcMotorEx intakeMotor;

    private Servo flickServo;
    private Servo loadServo;

    private ElapsedTime stateTimer = new ElapsedTime();

    private SpindexAutoLogic spindex = new SpindexAutoLogic();
    private ShooterLogic shooter = new ShooterLogic();

    private boolean artifactsToEat = false;

    public enum PathState {
        DRIVE_FROM_GOAL,
        DRIVE_TO_INTAKE,
        INTAKE_ON,
        DRIVE_TO_INTAKE_1,
        INDEX_1,
        DRIVE_TO_INTAKE_2,
        INDEX_2,
        DRIVE_TO_INTAKE_3,
        INDEX_3,
        DRIVE_TO_GOAL_1,
        INTAKE_1,
        STRAFE_OUT
    }

    private PathState pathState;

    private final Pose startPose = new Pose(24, 120, Math.toRadians(135));
    private final Pose shootPose = new Pose(48, 96, Math.toRadians(135));
    private final Pose intakeStart = new Pose(48, 84, Math.toRadians(180));
    private final Pose intakePosition1 = new Pose(36, 84, Math.toRadians(180));
    private final Pose intakePosition2 = new Pose(30, 84, Math.toRadians(180));
    private final Pose intakePosition3 = new Pose(24, 84, Math.toRadians(180));
    private final Pose intake1endPose = new Pose(24, 84, Math.toRadians(180));
    private final Pose intake1startPose = new Pose(48, 84, Math.toRadians(180));
    private final Pose endPose = new Pose(40, 88, Math.toRadians(135));

    private PathChain startToShoot, shootToEnd, intake1ToShoot, shootToIntake1, startToIntake1, intake1toIntake2, intake2toIntake3, intake3toShootPos;

    public void buildPaths() {
        startToShoot = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();
        intake1ToShoot = follower.pathBuilder()
                .addPath(new BezierLine(intake1endPose, shootPose))
                .setLinearHeadingInterpolation(intake1endPose.getHeading(), shootPose.getHeading())
                .build();
        startToIntake1 = follower.pathBuilder()
                .addPath(new BezierLine(intakeStart, intakePosition1))
                .setLinearHeadingInterpolation(intakeStart.getHeading(), intakePosition1.getHeading())
                .build();
        intake1toIntake2 = follower.pathBuilder()
                .addPath(new BezierLine(intakePosition1, intakePosition2))
                .setLinearHeadingInterpolation(intakePosition1.getHeading(), intakePosition2.getHeading())
                .build();
        intake2toIntake3 = follower.pathBuilder()
                .addPath(new BezierLine(intakePosition2, intakePosition3))
                .setLinearHeadingInterpolation(intakePosition2.getHeading(), intakePosition3.getHeading())
                .build();
        intake3toShootPos = follower.pathBuilder()
                .addPath(new BezierLine(intakePosition3, shootPose))
                .setLinearHeadingInterpolation(intakePosition3.getHeading(), shootPose.getHeading())
                .build();
        shootToIntake1 = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, intake1startPose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), intake1startPose.getHeading())
                .build();
        shootToEnd = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, endPose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), endPose.getHeading())
                .build();
    }

    public void statePathUpdatifier() {
        switch (pathState) {
            case DRIVE_FROM_GOAL:
                follower.followPath(startToShoot, true);
                setPathState(PathState.DRIVE_TO_INTAKE);
                break;
            case DRIVE_TO_INTAKE:
                if (!follower.isBusy()) {
                    follower.followPath(shootToIntake1);
                    setPathState(PathState.INTAKE_1);
                }
                break;
            case INTAKE_ON:
                if (!follower.isBusy()) {
                    intakeMotor.setPower(1);
                    setPathState(PathState.DRIVE_TO_INTAKE_1);
                }
                break;
            case DRIVE_TO_INTAKE_1:
                if (!follower.isBusy()) {
                    follower.followPath(startToIntake1);
                    setPathState(PathState.INDEX_1);
                }
                break;
            case INDEX_1:
                if (!follower.isBusy()) {
                    spinUseLeft();
                    stateTimer.reset();
                    setPathState(PathState.DRIVE_TO_INTAKE_2);
                }
                break;
            case DRIVE_TO_INTAKE_2:
                if (!follower.isBusy() || stateTimer.seconds() > 1) {
                    follower.followPath(intake1toIntake2);
                    setPathState(PathState.INDEX_2);
                }
                break;
            case INDEX_2:
                if (!follower.isBusy()) {
                    spinUseLeft();
                    stateTimer.reset();
                    flickServo.setPosition(-1);
                    if (stateTimer.seconds() > 1) {
                        flickServo.setPosition(0.5);
                        stateTimer.reset();
                        setPathState(PathState.DRIVE_TO_INTAKE_3);
                    }
                }
                break;
            case DRIVE_TO_INTAKE_3:
                if (!follower.isBusy() || stateTimer.seconds() > 1) {
                    follower.followPath(intake2toIntake3);
                    setPathState(PathState.INDEX_3);
                }
                break;
            case INDEX_3:
                if (!follower.isBusy()) {
                    spinUseLeft();
                    intakeMotor.setPower(0);
                    stateTimer.reset();
                    index = 3;
                    setPathState(PathState.DRIVE_TO_GOAL_1);
                }
                break;
            case DRIVE_TO_GOAL_1:
                if (!follower.isBusy() || stateTimer.seconds() > 1) {
                    follower.followPath(intake3toShootPos);
                    setPathState(PathState.STRAFE_OUT);
                }
                break;
            case STRAFE_OUT:
                if (!follower.isBusy()) {
                    follower.followPath(shootToEnd);
                    if (!follower.isBusy()) {
                        telemetry.addLine("Done all paths");
                    }
                }
                break;
            default:
                telemetry.addLine("Nothing running, all is good and boring");
                break;
        }
    }

    public void setPathState(PathState newState) {
        pathState = newState;
        pathTimer.resetTimer();
        artifactsToEat = false;

    }

    public void init() {
        pathState = PathState.DRIVE_FROM_GOAL;
        pathTimer = new Timer();
        opModeTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);
        shooter.init(hardwareMap);
        spindex.init(hardwareMap);
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        flickServo = hardwareMap.get(Servo.class, "shootGate1");
        loadServo = hardwareMap.get(Servo.class, "shootGate2");
        buildPaths();
        follower.setPose(startPose);
    }

    public void start() {
        opModeTimer.resetTimer();
        setPathState(pathState);
    }

    public void loop() {
        follower.update();
        shooter.update();
        spindex.update();
        statePathUpdatifier();

        telemetry.addData("Current Path State of Doom", pathState.toString());
        telemetry.addData("Robo-X (position that is)", follower.getPose().getX());
        telemetry.addData("Robo-Y (position that is)", follower.getPose().getY());
        telemetry.addData("Angle of bot/heading", follower.getPose().getHeading());
        telemetry.addData("Path of Time (path time)", pathTimer.getElapsedTimeSeconds());
    }
    void spinUseLeft() {
        index -= 1;
        spindexer.setTargetPosition(BASE_INDEXER_POS * (3 - index));
        spindexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        spindexer.setPower(0.3);
    }
}
