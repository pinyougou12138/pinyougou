package com.pinyougou.user.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtil;
import com.pinyougou.common.util.PhoneFormatCheckUtils;
import com.pinyougou.user.service.UserService;
import entity.Cart;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;

import com.github.pagehelper.PageInfo;
import entity.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @Reference
    private CartService cartService;

    /***
     * 查看购物车
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response) {
        //此处不需要判断是否登录
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        //redis中购物车
        List<Cart> redisCartList = cartService.getCartListFromRedis(username);
        if (redisCartList == null) {
            redisCartList = new ArrayList<>();
        }
        //cookie中的购物车
        List<Cart> cookieCartList = getCookieCartList(request);
        //合并两个购物车
        List<Cart> cartListNewMost = cartService.merge(cookieCartList, redisCartList);
        //清空cookie中的购物车
        CookieUtil.deleteCookie(request, response, "cartList");
        return cartListNewMost;
    }

    private List<Cart> getCookieCartList(HttpServletRequest request) {
        //cookie中查询
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", true);
        if (StringUtils.isEmpty(cartListStr)) {
            cartListStr = "[]";
        }
        List<Cart> cookieCartList = JSON.parseArray(cartListStr, Cart.class);
        return cookieCartList;
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAll();
    }


    @RequestMapping("/findPage")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                     @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return userService.findPage(pageNo, pageSize);
    }

    /***
     * 校验手机号码  生成验证码
     * @param phone
     */
    @RequestMapping("/sendCode")
    public Result sendCode(String phone) {
        if (!PhoneFormatCheckUtils.isPhoneLegal(phone)) {
            //手机号码格式不正确
            return new Result(false, "手机号格式不正确");
        }
        //生成验证码 发送验证码
        try {
            userService.createSmsCode(phone);
            return new Result(true, "验证码发送成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, "验证码发送失败");

    }

    /**
     * 增加
     *
     * @param user
     * @return
     */
    @RequestMapping("/add/{smsCode}")
    public Result add(@PathVariable("smsCode") String smsCode, @RequestBody TbUser user) {
        try {
            //判断验证码是否正确
            boolean flag = userService.checkSmsCode(user.getPhone(), smsCode);
            if (!flag) {
                return new Result(false, "验证码错误");
            }

            //密码加密
            String password = user.getPassword();
            String pwd = DigestUtils.md5DigestAsHex(password.getBytes());
            user.setPassword(pwd);
            //注册与修改时间
            user.setCreated(new Date());
            user.setUpdated(user.getCreated());
            userService.add(user);
            return new Result(true, "增加成功");

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param user
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbUser user) {
        try {
            userService.update(user);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public TbUser findOne1(@PathVariable(value = "id") Long id) {
        return userService.findOne(id);
    }
    @RequestMapping("/findOneByUsername")
    public TbUser findOne() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbUser tbUser = userService.findOne(username);
        return tbUser;
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        try {
            userService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }


    @RequestMapping("/search")
    public PageInfo<TbUser> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                     @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                     @RequestBody TbUser user) {
        return userService.findPage(pageNo, pageSize, user);
    }

}
