package com.pinyougou.user.service.impl;

import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbSeller;
import entity.UserOrderList;
import com.pinyougou.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class UserServiceImpl extends CoreServiceImpl<TbUser> implements UserService {
    @Override
    public TbUser findOne(Object username) {
        TbUser user = new TbUser();
        user.setUsername((String) username);
        TbUser tbUser = userMapper.selectOne(user);

        return tbUser;
    }

    private TbUserMapper userMapper;

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbSellerMapper sellerMapper;


    //短信模板
    @Value("${sign_name}")
    private String signName;

    @Value("${template_code}")
    private String templateCode;

    @Autowired
    public UserServiceImpl(TbUserMapper userMapper) {
        super(userMapper, TbUser.class);
        this.userMapper = userMapper;
    }


    @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<TbUser> all = userMapper.selectAll();
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }


    @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser user) {
        PageHelper.startPage(pageNo, pageSize);

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();

        if (user != null) {
            if (StringUtils.isNotBlank(user.getUsername())) {
                criteria.andLike("username", "%" + user.getUsername() + "%");
                //criteria.andUsernameLike("%"+user.getUsername()+"%");
            }
            if (StringUtils.isNotBlank(user.getPassword())) {
                criteria.andLike("password", "%" + user.getPassword() + "%");
                //criteria.andPasswordLike("%"+user.getPassword()+"%");
            }
            if (StringUtils.isNotBlank(user.getPhone())) {
                criteria.andLike("phone", "%" + user.getPhone() + "%");
                //criteria.andPhoneLike("%"+user.getPhone()+"%");
            }
            if (StringUtils.isNotBlank(user.getEmail())) {
                criteria.andLike("email", "%" + user.getEmail() + "%");
                //criteria.andEmailLike("%"+user.getEmail()+"%");
            }
            if (StringUtils.isNotBlank(user.getSourceType())) {
                criteria.andLike("sourceType", "%" + user.getSourceType() + "%");
                //criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
            }
            if (StringUtils.isNotBlank(user.getNickName())) {
                criteria.andLike("nickName", "%" + user.getNickName() + "%");
                //criteria.andNickNameLike("%"+user.getNickName()+"%");
            }
            if (StringUtils.isNotBlank(user.getJob())) {
                criteria.andLike("name", "%" + user.getJob() + "%");
                //criteria.andNameLike("%"+user.getName()+"%");
            }
            if (StringUtils.isNotBlank(user.getStatus())) {
                criteria.andLike("status", "%" + user.getStatus() + "%");
                //criteria.andStatusLike("%"+user.getStatus()+"%");
            }
            if (StringUtils.isNotBlank(user.getHeadPic())) {
                criteria.andLike("headPic", "%" + user.getHeadPic() + "%");
                //criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
            }
            if (StringUtils.isNotBlank(user.getQq())) {
                criteria.andLike("qq", "%" + user.getQq() + "%");
                //criteria.andQqLike("%"+user.getQq()+"%");
            }
            if (StringUtils.isNotBlank(user.getIsMobileCheck())) {
                criteria.andLike("isMobileCheck", "%" + user.getIsMobileCheck() + "%");
                //criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
            }
            if (StringUtils.isNotBlank(user.getIsEmailCheck())) {
                criteria.andLike("isEmailCheck", "%" + user.getIsEmailCheck() + "%");
                //criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
            }
            if (StringUtils.isNotBlank(user.getSex())) {
                criteria.andLike("sex", "%" + user.getSex() + "%");
                //criteria.andSexLike("%"+user.getSex()+"%");
            }

        }
        List<TbUser> all = userMapper.selectByExample(example);
        PageInfo<TbUser> info = new PageInfo<TbUser>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbUser> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

    /***
     * 生成验证码 发送验证码
     * @param phone
     */
    @Override
    public void createSmsCode(String phone) {
        try {
            //生成验证码
            String code = (long) ((Math.random() * 9 + 1) * 100000) + "";
            System.out.println(code);

            //存入redis
            redisTemplate.boundValueOps("Register_" + phone).set(code);

            //发送验证码
            /*Map map = new HashMap<String, String>();
            map.put("mobile", phone);
            map.put("sign_name", signName);
            map.put("template_code", templateCode);
            map.put("param", "{\"code\":\"" + code + "\"}");

            Message message = new Message("SMS_TOPIC", "SEND_MESSAGE_TAG", "createSmsCode", JSON.toJSONString(map).getBytes());
            defaultMQProducer.send(message);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断短信验证码是否存在
     *
     * @param phone
     * @return
     */
    @Override
    public boolean checkSmsCode(String phone, String code) {
        //根据phone获取redis中的验证码
        String redisCode = (String) redisTemplate.boundValueOps("Register_" + phone).get();
        if (StringUtils.isNotBlank(redisCode) && redisCode.equals(code)) {
            return true;
        }
        return false;
    }

}
