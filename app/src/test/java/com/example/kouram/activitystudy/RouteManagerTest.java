package com.example.kouram.activitystudy;

import com.skp.Tmap.TMapPoint;

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
        rm.createNewRoute();
        try{
            rm.createNewRoute();
            fail("routeManager는 반드시 한번에 하나의 path만 작업합니다.");
        } catch(RuntimeException e){
            // fine, as expected.
        }
    }

    @Test
    public void If_path_job_is_end_then_You_can_create_new_path(){
        RouteManager rm = new RouteManager();
        rm.createNewRoute();
        assertTrue(rm.hasCurrentWorkingRoute());

        rm.discardCurrentRoute();
        assertFalse(rm.hasCurrentWorkingRoute());

        rm.createNewRoute();
        assertTrue(rm.hasCurrentWorkingRoute());
    }

    @Test
    public void add_TMapPoint_to_current_path(){
        RouteManager rm = new RouteManager();

        try{
            TMapPoint point = new TMapPoint(1.0, 2.0);
            rm.add(point);
            fail("현재 생성된 route가 있어야지만 호출할 수 있습니다");
        }catch (RuntimeException e){
            // fine, as expected.
        }

        rm.createNewRoute();
        TMapPoint point = new TMapPoint(1.0, 2.0);
        rm.add(point);
    }

    @Test
    public void we_can_get_number_of_point_in_route(){
        //given:
        RouteManager rm = new RouteManager();
        rm.createNewRoute();
        //when:
        TMapPoint point = new TMapPoint(1.0, 2.0);
        rm.add(point);
        //then:
        assertEquals(rm.getNumOfPointInRoute(), 1);

        // and when add more points
        rm.add(point);
        rm.add(point);
        rm.add(point);
        rm.add(point);
        // then
        assertEquals(rm.getNumOfPointInRoute(), 5);

        // and when discard the route
        rm.discardCurrentRoute();
        // then # of points in route = 0
        assertEquals(rm.getNumOfPointInRoute(), 0);
    }
}