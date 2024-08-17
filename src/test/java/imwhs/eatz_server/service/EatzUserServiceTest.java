package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.user.EatzUser;
import imwhs.eatz_server.domain.user.Role;
import imwhs.eatz_server.repository.EatzUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EatzUserServiceTest {

    @Autowired EatzUserService eatzUserService;
    @Autowired EatzUserRepository eatzUserRepository;

    @Test
    public void 회원_가입() throws Exception {
        // given
        EatzUser user = new EatzUser("wonhee", Role.MEMBER);

        // when
        Long joinedUserId = eatzUserService.join(user);

        // then
        Assertions.assertEquals(user, eatzUserRepository.findOne(joinedUserId));
    }

    @Test
    public void 중복_사용자_이름_회원_예외_처리() throws Exception {
        // given
        EatzUser user1 = new EatzUser("wonhee", Role.MEMBER);
        EatzUser user2 = new EatzUser("wonhee", Role.MEMBER);

        // when
        eatzUserService.join(user1);

        assertThrows(IllegalStateException.class, () -> {
            eatzUserService.join(user2);
        });
    }

}