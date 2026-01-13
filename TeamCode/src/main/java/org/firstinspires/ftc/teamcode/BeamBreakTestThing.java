package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;

@TeleOp(name = "Beam Breaker Test Thing")
public class BeamBreakTestThing extends LinearOpMode {
    private DigitalChannel breakingBeams;
    private DcMotorEx spindexifier;

    private int index;
    private int currentPos;
    void spinIndex() {
        spindexifier.setTargetPosition(currentPos * index);
    }
    void spinUseRight() {
        index += 1;
//            if (index == 4) {
//                index = 1;
//            }
        spinIndex();
        spindexifier.setPower(0.3);
    }

    public void runOpMode() {
        breakingBeams = hardwareMap.get(DigitalChannel.class, "breakingBeams");
        spindexifier = hardwareMap.get(DcMotorEx.class, "spindexifier");

        spindexifier.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        spindexifier.setTargetPosition(752);
        spindexifier.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        boolean lastBroken = false;
        currentPos = -178;

        waitForStart();

        while (opModeIsActive()) {
            boolean beamInput = breakingBeams.getState();
            boolean isBroken = !beamInput;

            if (!lastBroken && isBroken) {
                spinUseRight();
            }

            lastBroken = isBroken;

            telemetry.addData("Is broken (raw)", breakingBeams);
            telemetry.addData("Is broken (parsed)", isBroken);
            telemetry.update();
        }
    }
}
