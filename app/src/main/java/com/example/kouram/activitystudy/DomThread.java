package com.example.kouram.activitystudy;

import com.skp.Tmap.TMapPoint;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomThread extends Thread{
    // 다운로드 받은 문자열을 저장할 변수
    private String xml;
    // 파싱한 결과를 저장할 리스트 - 몇개인지 모르므로 배열이 아니고 ArrayList로 만들어야 합니다.
    List<String> dataCoordinate = new ArrayList<String>();
    List<String> dataDescription = new ArrayList<String>();

    private TMapPoint start;
    private TMapPoint end;
    private ArrayList<TMapPoint> pass;




    private ArrayList<Tuple<Integer,String>> pathnav = new ArrayList<Tuple<Integer,String>>();
    private ArrayList<TMapPoint> pathData = new ArrayList<TMapPoint>();

    public ArrayList<Tuple<Integer, String>> getPathnav() {
        return pathnav;
    }

    public ArrayList<TMapPoint> getPathData() {
        return pathData;
    }



    public DomThread(TMapPoint _start, TMapPoint _end) {
        start = _start;
        end = _end;
        pass = null;
    }

    public DomThread(TMapPoint _start, TMapPoint _end, ArrayList<TMapPoint> _pass) {
        start = _start;
        end = _end;
        pass = _pass;
    }

    public TMapPoint WGS84toEPSG3857(TMapPoint point){ //API 좌표 -> 파싱 좌표
        double lat = Math.log(Math.tan((90 + point.getLatitude())*Math.PI / 360));
        lat = lat / (Math.PI/ 180);
        lat = lat * 20037508.34 / 180;

        double lon = point.getLongitude() * 20037508.34 / 180;
        return new TMapPoint(lat,lon);
    }

    public TMapPoint EPSG3857toWGS84(TMapPoint point){ //파싱 좌표 -> API 좌표

        double lat = point.getLatitude() * 180 / 20037508.34;
        lat = lat * (Math.PI/ 180);
        lat = Math.atan(Math.pow(Math.E,lat)) / (Math.PI / 360) - 90;

        double lon = point.getLongitude() / (20037508.34 / 180);

        return new TMapPoint(lat,lon);
    }

    public void run() {

        try {
            // 연결+옵션설정
            NumberFormat f = NumberFormat.getNumberInstance();
            f.setGroupingUsed(false);
            f.setMaximumFractionDigits(10);
            String addr = "https://apis.skplanetx.com/tmap/routes/pedestrian?version=1&format=xml";
            addr += "&startX=" + f.format(WGS84toEPSG3857(start).getLongitude());
            addr += "&startY=" + WGS84toEPSG3857(start).getLatitude();
            
            addr += "&endX=" + f.format(WGS84toEPSG3857(end).getLongitude());
            addr += "&endY=" + WGS84toEPSG3857(end).getLatitude();
            //System.out.println(WGS84toEPSG3857(end).getLongitude());

            addr += "&startName=" + URLEncoder.encode("출발지", "UTF-8");
            addr += "&endName=" + URLEncoder.encode("도착지", "UTF-8");

            //경유지가 있는 경우
            if(pass != null) {
                System.out.println("sasd");
                String str = new String();
                int i;
                for(i=0; i < pass.size()-1; i++){
                    str += WGS84toEPSG3857(pass.get(i)).getLongitude() +","+WGS84toEPSG3857(pass.get(i)).getLatitude()+"_";
                }
                str += WGS84toEPSG3857(pass.get(i)).getLongitude() +","+WGS84toEPSG3857(pass.get(i)).getLatitude();
                System.out.println("str: "+str);
                addr += "&searchOption=0";
                addr += "&passList=" + URLEncoder.encode(str, "UTF-8");
            }

            addr += "&appKey=9553cc22-8104-3088-a882-b90ef2a051d7";

            URL url = new URL(addr);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setConnectTimeout(10000);
            http.setUseCaches(false);

            // 위 부분 까지는 주소만 변경되고 모든 경우 동일

            // 위 주소에서 주는 데이터를 문자열로 읽기 위한 스트림 객체 생성
            BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                sb.append(line);
            }
            xml = sb.toString();
            br.close();
            http.disconnect();

        } catch (Exception e) {
            System.out.println("다운로드에러" + e.getMessage());

        }
        // System.out.println(xml);

        // 좌표 파싱
        try {
            // 자신의 static 메서드를 가지고 객체를 생성 : 싱글톤 패턴
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 다른 클래스의 객체를 가지고, 객체를 생성하면 팩토리 패턴.
            DocumentBuilder documentbuilder = factory.newDocumentBuilder(); //// 팩토리 메서드 패턴  공장에서 찍어줌
            // 문자열을 InputStream으로 변환
            InputStream is = new ByteArrayInputStream(xml.getBytes());
            Document doc = documentbuilder.parse(is);
            // xml을 메모리에 펼쳐놓고 루트를 elemnt에 저장
            Element element = doc.getDocumentElement();

            // 파싱할 태그의 리스트를 찾아온다
            // tmx 태그 전체를 list에 저장
            NodeList list = element.getElementsByTagName("coordinates");
            // 리스트를 순회하면서 데이터를 data에 추가
            for (int i = 0; i < list.getLength(); i++) {
                // i번째 tmx 태그를 node에 저장
                Node node = list.item(i);
                // 태그 내의 첫번째 값 앞으로 이동
                Node temp = node.getFirstChild();
                // 태그 내의 첫번째 값을 value에 저장
                String value = temp.getNodeValue();
                // 값을 data에 저장
                dataCoordinate.add(value);

            }

        } catch (Exception e) {
            System.out.println("파싱에러" + e.getMessage());
        }

        //안내 파싱
        try {
            // 자신의 static 메서드를 가지고 객체를 생성 : 싱글톤 패턴
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 다른 클래스의 객체를 가지고, 객체를 생성하면 팩토리 패턴.
            DocumentBuilder documentbuilder = factory.newDocumentBuilder(); //// 팩토리 메서드 패턴  공장에서 찍어줌
            // 문자열을 InputStream으로 변환
            InputStream is = new ByteArrayInputStream(xml.getBytes());
            Document doc = documentbuilder.parse(is);
            // xml을 메모리에 펼쳐놓고 루트를 elemnt에 저장
            Element element = doc.getDocumentElement();

            // 파싱할 태그의 리스트를 찾아온다
            // tmx 태그 전체를 list에 저장
            NodeList list = element.getElementsByTagName("description");
            // 리스트를 순회하면서 데이터를 data에 추가
            for (int i = 0; i < list.getLength(); i++) {
                // i번째 tmx 태그를 node에 저장
                Node node = list.item(i);
                // 태그 내의 첫번째 값 앞으로 이동
                Node temp = node.getFirstChild();
                // 태그 내의 첫번째 값을 value에 저장
                String value = temp.getNodeValue();
                // 값을 data에 저장
                dataDescription.add(value);

            }

        } catch (Exception e) {
            System.out.println("파싱에러" + e.getMessage());
        }

        //파싱 데이터 처리

        ArrayList<String> tmpArray = new ArrayList<String>(); //문자열 처리가 완료되어 하나의 element씩 들어감
        int headIndex=0;

        for(int j=0; j < dataCoordinate.size(); j++){
            System.out.println(j+":"+dataCoordinate.get(j));
            String[] splt = dataCoordinate.get(j).replace(',',' ').trim().split("  | ");
            for (int i=0; i < splt.length;i++){
                if (i == 0) {
                    pathnav.add(new Tuple<Integer,String>(headIndex/2,dataDescription.get(j)));//튜플 구성
                }
                tmpArray.add(splt[i]);
                headIndex++;
            }
        }

        for(int i=0; i < pathnav.size(); i++)
            System.out.println(pathnav.get(i).left + " | " + pathnav.get(i).right);

        for(int j=0; j < dataDescription.size(); j++){
            System.out.println(j+":"+dataDescription.get(j));
        }


        //pathdata를 실제 사용할 좌표계 주소로 변환하여 만듬
        for(int i=0; i < tmpArray.size(); i=i+2) {
            TMapPoint tp = new TMapPoint(Double.parseDouble(tmpArray.get(i + 1)), Double.parseDouble(tmpArray.get(i)));
            pathData.add(EPSG3857toWGS84(tp));
        }
    }
}

