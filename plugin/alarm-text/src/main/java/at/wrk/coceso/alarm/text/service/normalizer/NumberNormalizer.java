package at.wrk.coceso.alarm.text.service.normalizer;

public interface NumberNormalizer {
    String getSupportedUriSchema();

    String normalize(String inputNumber);
}
