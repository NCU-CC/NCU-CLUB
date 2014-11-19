package evan.ncu.club;

/**
 * Created by Evany on 2014/11/2.
 */
public class ListData {
    public String title;
    public String place;
    public String time;
    public String content;

    public ListData(String m_title, String m_place, String m_time, String m_content){
        title = m_title;
        place = m_place;
        time = m_time;
        content = m_content;
    }
    public ListData(String m_title, String m_time, String m_content){
        title = m_title;
        time = m_time;
        content = m_content;
    }


}
