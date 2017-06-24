package com.example.kouram.activitystudy;

import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by Ko U Ram on 2017-06-24.

public class TourManagerTest {
    @Test
    public void Create_new_path_cannot_be_duplicated(){
        TourManager tm = new TourManager();
        tm.createNewTour();
        try{
            tm.createNewTour();
            fail("routeManager는 반드시 한번에 하나의 tour만 작업합니다.");
        } catch(RuntimeException e){
            // fine, as expected.
        }
    }

    @Test
    public void If_tour_job_is_end_then_You_can_create_new_tour(){
        TourManager tm = new TourManager();
        tm.createNewTour();
        assertTrue(tm.hasCurrentWorkingTour());

        tm.discardCurrentTour();
        assertFalse(tm.hasCurrentWorkingTour());

        tm.createNewTour();
        assertTrue(tm.hasCurrentWorkingTour());
    }

    @Test
    public void add_path_to_current_tour(){
        TourManager tm = new TourManager();

        TMapPoint p1 = new TMapPoint(1.0, 1.1);
        TMapPoint p2 = new TMapPoint(2.0, 2.1);
        TMapPoint p3 = new TMapPoint(3.0, 3.1);
        TMapPoint p4 = new TMapPoint(4.0, 4.1);

        TMapPolyLine path = new TMapPolyLine();
        path.addLinePoint(p1);
        path.addLinePoint(p2);
        path.addLinePoint(p3);
        path.addLinePoint(p4);

        try{
            tm.setCurrent(path);
            fail("현재 생성된 route가 있어야지만 호출할 수 있습니다");
        }catch (RuntimeException e){
            // fine, as expected.
        }

        tm.createNewTour();
        tm.setCurrent(path);

        //tm.saveAndDiscardCurrentTour();
    }

    // 실제 mock이 아님.. 몰라서 걍 써둠.
    // db를 그냥 쓰면 컴파일을 안 해주니까 별 수 없음..
    DBManager mockDB = null;
    @Test
    public void If_path_wasnt_set_but_save_tour_Then_throw_exception(){
        // given new tour
        TourManager tm = new TourManager();
        try{
            tm.saveAndDiscardCurrentTour(mockDB);
            fail("현재 생성된 tour가 있어야지만 호출할 수 있습니다");
        }catch (RuntimeException e){
            // fine, as expected.
        }

        // when save tour that hasn't a path
        tm.createNewTour();

        // then throw exception
        try{
            tm.saveAndDiscardCurrentTour(mockDB);
            fail("path는 반드시 저장되어야합니다.");
        }catch (RuntimeException e){
            // fine, as expected.
        }

        TMapPolyLine path = new TMapPolyLine();
        tm.setCurrent(path);

        try{
            tm.saveAndDiscardCurrentTour(mockDB);
            fail("navigation info는 반드시 저장되어야합니다.");
        }catch (RuntimeException e){
            // fine, as expected.
        }

        ArrayList<Tuple<Integer,String>> navInfos = new ArrayList<>();
        tm.setCurrent(navInfos);
        tm.saveAndDiscardCurrentTour(mockDB);

        try{
            tm.saveAndDiscardCurrentTour(mockDB);
            fail("currentTour는 discard되어야 합니다.");
        }catch (RuntimeException e){
            assertEquals(e.getMessage(), "need to create tour first. call createNewTour");
            // fine, as expected.
        }
    }
}
 */
