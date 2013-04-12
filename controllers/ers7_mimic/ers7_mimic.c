/*
 * File:         ers210.c
 * Date:         August 26th, 2002
 * Description:  A controller for the real Aibo ERS-210 robot.
 * Author:       Lukas Hohl
 * Copyright (c) 2008 Cyberbotics - www.cyberbotics.com
 */

/* 
 * this controller is quite entertaining: releasing the servos on one of the robot's
 * legs so that it hangs loose, it makes the remaining legs 'mimic' that one as it 
 * gets rotated or bent by hand (author: Lukas Hohl) 
 */

#include <webots/robot.h>
#include <webots/servo.h>

#define SIMULATION_STEP 16
#define NUM_JOINTS_PER_LEG 3

enum {                          /* leg indices enumeration */
    LFLEG,                      /* left fore leg */
    LHLEG,                      /* left hind leg */
    RFLEG,                      /* right fore leg */
    RHLEG,                      /* right hind leg */
    NUM_LEGS
};

#define MASTER_LEG RFLEG        /* master leg */

static WbDeviceTag leg_servos[NUM_LEGS][NUM_JOINTS_PER_LEG];

int main()
{
    leg_servos[LFLEG][0] = wb_robot_get_device("PRM:/r2/c1-Joint2:21");
    leg_servos[LFLEG][1] = wb_robot_get_device("PRM:/r2/c1/c2-Joint2:22");
    leg_servos[LFLEG][2] = wb_robot_get_device("PRM:/r2/c1/c2/c3-Joint2:23");
    leg_servos[LHLEG][0] = wb_robot_get_device("PRM:/r3/c1-Joint2:31");
    leg_servos[LHLEG][1] = wb_robot_get_device("PRM:/r3/c1/c2-Joint2:32");
    leg_servos[LHLEG][2] = wb_robot_get_device("PRM:/r3/c1/c2/c3-Joint2:33");
    leg_servos[RFLEG][0] = wb_robot_get_device("PRM:/r4/c1-Joint2:41");
    leg_servos[RFLEG][1] = wb_robot_get_device("PRM:/r4/c1/c2-Joint2:42");
    leg_servos[RFLEG][2] = wb_robot_get_device("PRM:/r4/c1/c2/c3-Joint2:43");
    leg_servos[RHLEG][0] = wb_robot_get_device("PRM:/r5/c1-Joint2:51");
    leg_servos[RHLEG][1] = wb_robot_get_device("PRM:/r5/c1/c2-Joint2:52");
    leg_servos[RHLEG][2] = wb_robot_get_device("PRM:/r5/c1/c2/c3-Joint2:53");

    /* release master leg servos, enable position reading */
    int j;
    for (j = 0; j < NUM_JOINTS_PER_LEG; j++) {
        wb_servo_set_motor_force(leg_servos[MASTER_LEG][j], 0.0);
        wb_servo_enable_position(leg_servos[MASTER_LEG][j], SIMULATION_STEP);
    }

    while (wb_robot_step(SIMULATION_STEP) != -1) {
      int j;
      for (j = 0; j < NUM_JOINTS_PER_LEG; j++) {
          /* master position */
          double master = wb_servo_get_position(leg_servos[MASTER_LEG][j]);
          int i;
          for (i = 0; i < NUM_LEGS; i++) {
              if (i != MASTER_LEG) {
                  /* set remaining legs to master */
                  wb_servo_set_position(leg_servos[i][j], master);   
              }
          }
      }
    }
    
    wb_robot_cleanup();

    return 0;
}
