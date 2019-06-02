package com.huadong.spoon.mybatis.typehandler;

import com.huadong.spoon.utils.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by jinjinhui on 2018/4/19.
 */
public class CalendarTypeHandler implements TypeHandler<Calendar> {

    @Override
    public Calendar getResult(ResultSet rs, String string) throws SQLException{
        if(StringUtils.isBlank(string)){
            return null;
        }
        Timestamp time = rs.getTimestamp(string);
        if(time == null){
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time.getTime());
        return c;
    }

    @Override
    public Calendar getResult(ResultSet rs, int i) throws SQLException {
        if(i == 0){
            return null;
        }
        Timestamp time = rs.getTimestamp(i);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time.getTime());
        return c;
    }

    @Override
    public Calendar getResult(CallableStatement cs, int i) throws SQLException{
        if(i == 0){
            return null;
        }
        Timestamp time = cs.getTimestamp(i);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time.getTime());
        return c;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, Calendar t, JdbcType jt) throws SQLException {
        ps.setTimestamp(i, new Timestamp(t.getTimeInMillis()));
    }

}