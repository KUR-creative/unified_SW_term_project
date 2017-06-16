package com.example.kouram.activitystudy;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RouteManagerTest {
    @Test
    public void Create_new_path_cannot_be_duplicated(){
        RouteManager rm = new RouteManager();
        rm.createNewPath();
        try{
            rm.createNewPath();
            fail("routeManager는 반드시 한번에 하나의 path만 작업합니다.");
        }catch (RuntimeException e){
            // fine, as expected.
        }
    }
    @Test
    public void If_path_job_is_end_then_You_can_create_new_path(){
        RouteManager rm = new RouteManager();
        rm.createNewPath();
        rm.discardCurrentPath();
        rm.createNewPath();
    }
}