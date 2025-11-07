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

@Autonomous(name = "AprilTag Alignment Movement OpMode Thing")
public class AprilTagLimelightMovement extends OpMode {
    private Limelight3A limelight;
    private IMU imu;
    private LLResult llResult;
    private Pose3D botPose;

    private double botX;
    private double botY;

    @Override
    public void init() {
        // Initialize Limelight
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // Switch the pipeline to 0
        limelight.pipelineSwitch(0);

        // Initialize IMU with hub orientation settings
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

        // Limelight initialization
        llResult = limelightInit();

        // Limelight loop, detects AprilTags and robot position
        if (validAprilTag()) {
            botPose = llResult.getBotpose_MT2();

            // Bot detection loop
            if (botPose != null) {
                getBotPos();
            }

            // Get necessary robot telemetry
            botTelemetry();
            telemetry.update();
        }
    }

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
            - botX: Current robot X position in relation to estimated location on field
            - botY: Current robot Y position in relation to estimated location on field
     */
    public void getBotPos() {
        // Get bot positions for later use
        botX = botPose.getPosition().x;
        botY = botPose.getPosition().y;
    }

    /*
        Purpose: Grabs telemetry of robot position and AprilTag ID, family, and position
        Arguments: still *beep*ing none
        Returns: "technically" nothing
        Global variables:
            - botX: Current robot X position in relation to estimated location on field
            - botY: Current robot Y position in relation to estimated location on field
            - llResult: Current Limelight results (see limelightInit)
            - telemetry: Global telemetry object, adds data to update on telemetry.update()
     */
    public void botTelemetry () {
        // Get robot location using MegaTag 2 and bot positions
        getBotPos();
        telemetry.addData("MT2 Location:", "(" + botX + ", " + botY + ")");

        // Get AprilTag IDs, positions, and family
        List<LLResultTypes.FiducialResult> fiducialResults = llResult.getFiducialResults();
        for (LLResultTypes.FiducialResult fr : fiducialResults) {
            telemetry.addData("Fiducial", "ID: %d, Family: %s, X: %.2f, Y: %.2f", fr.getFiducialId(), fr.getFamily(), fr.getTargetXDegrees(), fr.getTargetYDegrees());
        }
    }

    public void moveOnAprilTag() {
        // unused for now...
    }
}