package com.knuissant.dailyq.external.ncp.clova;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.knuissant.dailyq.config.NcpConfig;
import com.knuissant.dailyq.exception.ErrorCode;
import com.knuissant.dailyq.exception.InfraException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClovaSpeechClient {

    private final RestClient ncpClovaRestClient;
    private final NcpConfig ncpConfig;

    /**
     * Object Storage의 파일로 STT 변환 요청
     *
     * @param dataKey   (스토리지의 '파일 키(key)', 예: "UUID.mp3")
     * @param sttTaskId (콜백 URL에 포함시킬 sttTask ID, 예: 101)
     */
    public String requestTranscription(String dataKey, Long sttTaskId) {
        Map<String, Object> body = getBody(dataKey, sttTaskId);

        try {
            log.debug("Requesting CLOVA STT. sttTaskId={}, dataKey={}", sttTaskId, dataKey);
            ClovaSttTaskResponse response = ncpClovaRestClient.post()
                    .uri("/recognizer/object-storage")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(ClovaSttTaskResponse.class);

            if (response == null || response.token() == null) {
                throw new InfraException(ErrorCode.NCP_API_COMMUNICATION_ERROR, "Clova API가 토큰을 반환하지 않았습니다.");
            }

            return response.token();

        } catch (Exception e) {
            throw new InfraException(ErrorCode.NCP_API_COMMUNICATION_ERROR, e);
        }
    }

    private Map<String, Object> getBody(String dataKey, Long sttTaskId) {
        String callbackUrl = ncpConfig.getClovaCallbackServer() + "/api/stt/callback/" + sttTaskId;

        Map<String, Object> diarization = new HashMap<>();
        diarization.put("enable", false);

        Map<String, Object> body = new HashMap<>();
        body.put("dataKey", dataKey);
        body.put("language", "ko-KR");
        body.put("completion", "async");
        body.put("callback", callbackUrl);
        body.put("wordAlignment", true);
        body.put("fullText", true);
        body.put("diarization", diarization);
        return body;
    }
}
