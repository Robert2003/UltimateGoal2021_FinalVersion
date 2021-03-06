package org.firstinspires.ftc.teamcode.T265;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.EverythingForTeleOP.RobotDefinition_ForTeleOP;
import org.firstinspires.ftc.teamcode.FromRoadRunner.SampleMecanumDrive;

@TeleOp(group = "advanced")
@Disabled
public class TeleOP_T265 extends LinearOpMode {
    // Define 2 states, drive control or automatic control
    enum Mode {
        DRIVER_CONTROL,
        AUTOMATIC_CONTROL
    }


    Mode currentMode = Mode.DRIVER_CONTROL;

    private ElapsedTime runtime = new ElapsedTime();

    boolean supress2 = false, buttonPressed = false;
    double second_surpress = 1;

    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap, false);

        RobotDefinition_ForTeleOP robot = new RobotDefinition_ForTeleOP();
        robot.init(hardwareMap);

        waitForStart();
        runtime.reset();

        if (isStopRequested()) return;

        while (opModeIsActive() && !isStopRequested()) {
            // Update the drive class
            drive.update();

            // Read pose
            Pose2d poseEstimate = drive.getPoseEstimate();

            if(gamepad1.dpad_up)
                robot.intakeServo.setPosition(1);
            if(gamepad1.dpad_down)
                robot.intakeServo.setPosition(0.42);

            if(gamepad1.left_bumper)
            {
                if(!buttonPressed)
                {
                    supress2 = !supress2;
                    buttonPressed = true;
                }
            }
            else
                buttonPressed = false;

            if(supress2)
                second_surpress = 0.6;
            else
                second_surpress = 1;

            // We follow different logic based on whether we are in manual driver control or switch
            // control to the automatic mode
            switch (currentMode)
            {
                case DRIVER_CONTROL:
                    if(gamepad1.right_bumper)
                        drive.setWeightedDrivePower(
                                new Pose2d(
                                        gamepad1.right_stick_y*0.30,
                                        gamepad1.right_stick_x*0.30,
                                        (-gamepad1.right_trigger + gamepad1.left_trigger)*0.30
                                )
                        );
                    else
                        drive.setWeightedDrivePower(
                                new Pose2d(
                                        gamepad1.right_stick_y*second_surpress,
                                        gamepad1.right_stick_x*second_surpress,
                                        (-gamepad1.right_trigger + gamepad1.left_trigger)*second_surpress
                                )
                        );

                    if(gamepad2.left_bumper)
                    {
                        if(runtime.seconds() > 1)
                        {
                            runtime.reset();
                            robot.servo.setPosition(0);
                        }
                        else if(runtime.seconds() > 0.5)
                            robot.servo.setPosition(0.55);
                    }
                    else
                        robot.servo.setPosition(0.55);
                    robot.Gamepad1Actions(gamepad1);
                    robot.Gamepad2Actions(gamepad2, poseEstimate.getX(), poseEstimate.getY());

                    if(gamepad1.y)
                    {

                    }
                    break;
            }
        }
    }
}
