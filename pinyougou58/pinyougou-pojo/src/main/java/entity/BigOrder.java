package entity;

import com.pinyougou.pojo.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

/**
 * @Author:Drever
 * @Date: 2019/7/27  上午 10:50
 * Created by:Prettydongdong@outlook.com
 */
public class BigOrder implements Serializable {
    private Integer id;
    private String titleName;
    private Double money;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }
}
