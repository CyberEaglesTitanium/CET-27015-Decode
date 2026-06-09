package org.firstinspires.ftc.teamcode.limelight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.List;

public class LimelightUtils {
    public Limelight3A limelight;
    public IMU imu;
    public LLResult llResult;
    public Pose3D botPose;
    private double botX;
    private double botY;
    public Telemetry telemetry;
    private String robotSortOrder;
    private DcMotorEx frontLeft;
    private DcMotorEx frontRight;
    private DcMotorEx backLeft;
    private DcMotorEx backRight;

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
