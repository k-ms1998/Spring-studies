package com.fc.notice_board.notice_board.dto.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.fc.notice_board.notice_board.dto.security.KakaoOAuth2Response.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DTO - Kakao OAuth 2.0 Response Test")
class KakaoOAuth2ResponseTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @DisplayName("Given Map from Json - Return KakaoOAuth2Response Object")
    @Test
    void givenMapFromJson_whenInstantiating_thenReturnKakaoOAuth2ResponseObject() throws Exception {
        // Given
        String serializedResponse = """
                {
                    "id": 1234567890,
                    "connected_at": "2023-01-10T00:00:00Z",
                    "properties":{
                        "nickname": "kms"
                    },
                    "kakao_account": {
                        "profileNicknameNeedsAgreement": false,
                        "profile": {
                            "nickname": "kmsNickname"
                        },
                        "has_email": true,
                        "email_needs_agreement": false,
                        "is_email_valid": true,
                        "is_email_verified": true,
                        "email": "test@email.com"
                    }
                }
                """;

        /**
         * serializedResponse 를 자동으로 (K, V) 형식의 데이터로 변환
         */
        Map<String, Object> attributes = mapper.readValue(serializedResponse, new TypeReference<>(){});

        // When
        /**
         * from 메서드를 호출헤서, (K,V) 데이터를 객체로 변환
         */
        KakaoOAuth2Response result = KakaoOAuth2Response.from(attributes);

        // Then
        Assertions.assertThat(result)
                .hasFieldOrPropertyWithValue("id", 1234567890L)
                .hasFieldOrPropertyWithValue("kakaoAccount.profileNicknameNeedsAgreement", false)
                .hasFieldOrPropertyWithValue("kakaoAccount.profile.nickname", "kmsNickname");

    }

}