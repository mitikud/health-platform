package com.medical.diagnosisservice.service.LLM.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.medical.diagnosisservice.dto.DiagnosisPayload;
import com.medical.diagnosisservice.service.LLM.LlmClient;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("prod")
@Component("cloudLlm")
public class OpenAiLlmClient implements LlmClient {

    @Value("${ai.openai.apiKey}") private String apiKey;
    @Value("${ai.openai.model:gpt-4o-mini}") private String model;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final String SYSTEM = """
  You are a cautious clinical assistant. You DO NOT provide medical diagnosis, only
  a list of POSSIBLE conditions and next steps. Output strict JSON with keys:
  { "possible": ["..."], "recommendations": ["..."] }
  Keep language appropriate to the user's locale (en/am/ti).
  """;

//    @Override
//    public DiagnosisPayload suggestDiagnosis(String symptoms, String locale) throws Exception {
//        var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
//        var reqJson = mapper.createObjectNode();
//        reqJson.put("model", model);
//        var msgs = reqJson.putArray("messages");
//        msgs.add(mapper.createObjectNode().put("role", "system").put("content", SYSTEM));
//        msgs.add(mapper.createObjectNode().put("role", "user").put("content",
//                "Locale: " + locale + "\nSymptoms: " + symptoms));
//        reqJson.put("response_format", mapper.createObjectNode().put("type", "json_object"));
//
//        var client = new okhttp3.OkHttpClient();
//        var body = okhttp3.RequestBody.create(
//                reqJson.toString(), okhttp3.MediaType.parse("application/json"));
//        var request = new okhttp3.Request.Builder()
//                .url("https://api.openai.com/v1/chat/completions")
//                .addHeader("Authorization", "Bearer " + apiKey)
//                .post(body).build();
//
//        try (var resp = client.newCall(request).execute()) {
//            if (!resp.isSuccessful()) throw new RuntimeException("LLM failed: "+resp.code());
//            var root = mapper.readTree(resp.body().string());
//            var content = root.path("choices").get(0).path("message").path("content").asText();
//            return content; // JSON string
//        }
//    }
@Override
public DiagnosisPayload suggestDiagnosis(String symptoms, String locale) throws Exception {
    ObjectNode root = mapper.createObjectNode();
    root.put("model", model);
    var msgs = root.putArray("messages");
    msgs.add(mapper.createObjectNode().put("role","system").put("content", SYSTEM));
    msgs.add(mapper.createObjectNode().put("role","user")
            .put("content", "Locale: " + locale + "\nSymptoms: " + symptoms));
    root.set("response_format", mapper.createObjectNode().put("type","json_object"));

    OkHttpClient http = new OkHttpClient();
    Request req = new Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer " + apiKey)
            .post(RequestBody.create(root.toString(), MediaType.parse("application/json")))
            .build();

    try (Response res = http.newCall(req).execute()) {
        if (!res.isSuccessful()) throw new RuntimeException("LLM " + res.code());
        var text = mapper.readTree(res.body().string())
                .path("choices").get(0).path("message").path("content").asText();
        return mapper.readValue(text, DiagnosisPayload.class);
    }
}
}

