package imwhs.eatz_server.service;

import imwhs.eatz_server.domain.user.EatzUser;
import imwhs.eatz_server.repository.EatzUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EatzUserService {

    private final EatzUserRepository eatzUserRepository;

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(EatzUser eatzUser) {
        validateDuplicateUser(eatzUser);
        eatzUserRepository.save(eatzUser);
        return eatzUser.getId();
    }

    /**
     * 전체 회원 조회
     */
    public List<EatzUser> findAllUsers() {
        return eatzUserRepository.findAll();
    }

    /**
     * id로 특정 회원 조회
     */
    public EatzUser findById(Long id) {
        return eatzUserRepository.findOne(id);
    }

    /**
     * 중복 회원 여부 검증
     */
    public void validateDuplicateUser(EatzUser eatzUser) {
        List<EatzUser> users = eatzUserRepository.findByUsername(eatzUser.getUsername());

        if (!users.isEmpty()) {
            throw new IllegalStateException("이미 동일한 사용자 이름을 가진 회원이 존재합니다.");
        }
    }


}
