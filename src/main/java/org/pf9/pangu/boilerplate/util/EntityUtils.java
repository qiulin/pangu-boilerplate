package org.pf9.pangu.boilerplate.util;

import org.pf9.pangu.framework.auth.security.SecurityUtils;
import org.pf9.pangu.framework.data.domain.AbstractAuditingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class EntityUtils {


    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};


    // 生成8位uuid码
    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();

    }

    public static String getOrderId() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(new Date());
        String orderid = dateString
                + (String.valueOf(System.nanoTime())).substring(7, 13);
        return orderid;

    }

    /**
     * 密码强度校验
     *
     * @return Z = 字母 S = 数字 T = 特殊字符
     */
    public static String checkPassword(String passwordStr) {

        //匹配数字重复零次或多次
        String regexS = "\\d*";
        String regexZ = "[a-zA-Z]+";
        String regexT = "\\W+$";
        String regexZT = "\\D*";
        String regexST = "[\\d\\W]*";
        String regexZS = "\\w*";
        String regexZST = "[\\w\\W]*";

        if (passwordStr.matches(regexZ)) {
            return "weak";
        }
        if (passwordStr.matches(regexS)) {
            return "weak";
        }
        if (passwordStr.matches(regexT)) {
            return "weak";
        }
        if (passwordStr.matches(regexZT)) {
            return "strong";
        }
        if (passwordStr.matches(regexST)) {
            return "strong";
        }
        if (passwordStr.matches(regexZS)) {
            return "strong";
        }
        if (passwordStr.matches(regexZST)) {
            return "veryStrong";
        }
        return passwordStr;

    }


    /**
     * 复制对象属性（对象类型必须相同）
     *
     * @param orig       资源对象
     * @param dest       目标对象
     * @param ignoreNull 是否忽略空（true:忽略，false：不忽略）
     */

    public static <T> T copyProperties(T orig, T dest, boolean ignoreNull) {
        if (orig == null || dest == null)
            return null;
        return copyProperties(orig, dest, orig.getClass(), ignoreNull);
    }


    public static <T> T copyProperties(T orig, T dest, Class<?> clazz,
                                       boolean ignoreNull) {
        if (orig == null || dest == null)
            return null;
        if (!clazz.isAssignableFrom(orig.getClass()))
            return null;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(orig);
                if (!Modifier.isFinal(field.getModifiers())) {
                    if (!(ignoreNull && value == null)) {
                        field.set(dest, value);
                    }
                }
                field.setAccessible(false);
            } catch (Exception e) {
            }
        }
        if (clazz.getSuperclass() == Object.class) {
            return dest;
        }
        return copyProperties(orig, dest, clazz.getSuperclass(), ignoreNull);
    }


    public static <T extends AbstractAuditingEntity> void setModifiedAudit(T entity) {
        entity.setLastModifiedBy(SecurityUtils.getCurrentUserLogin());
        entity.setLastModifiedDate(Instant.now());
    }

    public static <T extends AbstractAuditingEntity> void setCreatedAudit(T entity) {
        entity.setCreatedBy(SecurityUtils.getCurrentUserLogin());
        entity.setCreatedDate(Instant.now());
        EntityUtils.setModifiedAudit(entity);
    }
}
