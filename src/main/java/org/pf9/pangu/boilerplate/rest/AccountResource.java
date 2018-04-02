package org.pf9.pangu.boilerplate.rest;

import org.pf9.pangu.framework.auth.security.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.pf9.pangu.boilerplate.constants.APIRouteConstants;
import org.pf9.pangu.boilerplate.entity.User;
import org.pf9.pangu.boilerplate.rest.vm.ManagedUserVM;
import org.pf9.pangu.boilerplate.service.UserService;
import org.pf9.pangu.boilerplate.service.dto.UserDTO;
import org.pf9.pangu.boilerplate.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping(APIRouteConstants.API_V1_ROOT)
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);


    private final UserService userService;


    public AccountResource(UserService userService) {

        this.userService = userService;
    }

    /**
     * POST  /register : register the user.
     *
     * @param managedUserVM the managed user View Model
     * @return the ResponseEntity with status 201 (Created) if the user is registered or 400 (Bad Request) if the login or e-mail is already in use
     */
    @PostMapping(path = "/register",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {

        HttpHeaders textPlainHeaders = new HttpHeaders();
        textPlainHeaders.setContentType(MediaType.TEXT_PLAIN);

        return userService.findUserByLogin(managedUserVM.getLogin().toLowerCase())
                .map(user -> new ResponseEntity<>("login already in use", textPlainHeaders, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> userService.findUserByEmail(managedUserVM.getEmail())
                        .map(user -> new ResponseEntity<>("e-mail address already in use", textPlainHeaders, HttpStatus.BAD_REQUEST))
                        .orElseGet(() -> {
                            User user = userService
                                    .createUser(managedUserVM.getLogin(), managedUserVM.getPassword(),
                                            managedUserVM.getFirstName(), managedUserVM.getLastName(),
                                            managedUserVM.getEmail().toLowerCase(), managedUserVM.getImageUrl(), managedUserVM.getLangKey());

                            return new ResponseEntity<>(HttpStatus.CREATED);
                        })
                );
    }

    /**
     * GET  /authenticate : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request
     * @return the login if the user is authenticated
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /account : get the current user.
     *
     * @return the ResponseEntity with status 200 (OK) and the current user in body, or status 500 (Internal Server Error) if the user couldn't be returned
     */
    @GetMapping("/account")
    public ResponseEntity<UserDTO> getAccount() {
        return Optional.ofNullable(userService.getCurrentUser())
                .map(user -> new ResponseEntity<>(new UserDTO(user), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * POST  /account : update the current user information.
     *
     * @param userDTO the current user information
     * @return the ResponseEntity with status 200 (OK), or status 400 (Bad Request) or 500 (Internal Server Error) if the user couldn't be updated
     */
    @PostMapping("/account")
    public ResponseEntity saveAccount(@Valid @RequestBody UserDTO userDTO) {
        Optional<User> existingUser = userService.findUserByEmail(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userDTO.getLogin()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("user-management", "emailexists", "Email already in use")).body(null);
        }
        return userService
                .findUserByLogin(SecurityUtils.getCurrentUserLogin())
                .map(u -> {
                    userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
                            userDTO.getLangKey());
                    return new ResponseEntity(HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * POST  /account/change_password : changes the current user's password
     *
     * @return the ResponseEntity with status 200 (OK), or status 400 (Bad Request) if the new password is not strong enough
     */
    @PostMapping(path = "/account/change_password",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity changePassword(@RequestBody HashMap req) {
        String password = req.get("password").toString();
        if (!checkPasswordLength(password)) {
            return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
        }
        userService.changePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(path = "/account/verifyCode")
    public void Yzm(HttpSession session, HttpServletResponse resp) {
        // 验证码图片的宽度。
        int width = 60;

        // 验证码图片的高度。
        int height = 20;

        // 验证码字符个数
        int codeCount = 4;

        int x = 0;

        // 字体高度
        int fontHeight;

        int codeY;

        char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        x = width / (codeCount + 1);
        fontHeight = height - 2;
        codeY = height - 4;
        BufferedImage buffImg = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();

        // 创建一个随机数生成器类
        Random random = new Random();

        // 将图像填充为白色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.PLAIN, fontHeight);
        // 设置字体。
        g.setFont(font);

        // 画边框。
//        g.setColor(Color.BLACK);
//        g.drawRect(0, 0, width - 1, height - 1);

        // 随机产生160条干扰线，使图象中的认证码不易被其它程序探测到。
        g.setColor(Color.BLACK);
        for (int i = 0; i < 1; i++) {
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x2, y2, x + xl, y2 + yl);
        }

        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();

        int red = 0, green = 0, blue = 0;

        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < codeCount; i++) {
            // 得到随机产生的验证码数字。
            String strRand = String.valueOf(codeSequence[random.nextInt(36)]);
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);

            // 用随机产生的颜色将验证码绘制到图像中。
            g.setColor(new Color(red, green, blue));
            g.drawString(strRand, (i + 1) * x, codeY);

            // 将产生的四个随机数组合在一起。
            randomCode.append(strRand);
        }
        // 将四位数字的验证码保存到Session中。
        session.setAttribute("validateCode", randomCode.toString());
        ServletOutputStream sos = null;
        try {
            sos = resp.getOutputStream();
            ImageIO.write(buffImg, "jpeg", sos);
            sos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (sos != null) {
                try {
                    sos.close();
                    sos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }


    private boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
                password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
                password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }
}
