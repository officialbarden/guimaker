package dev.guimaker.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.StringReader;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaceholderResolverTest {
    @Test
    void keyValueEmbeddedInJsonStringIsJsonEscaped() {
        String malicious = "\"}, {\"text\":\"pwned\\line\nnext";
        String command = "tellraw @s {\"text\":\"Hello {key:name}\"}";

        String resolved = PlaceholderResolver.resolveKeyValues(command, Map.of("name", malicious));
        JsonObject payload = JsonParser.parseString(resolved.substring(resolved.indexOf('{'))).getAsJsonObject();

        assertEquals("Hello " + malicious, payload.get("text").getAsString());
        assertEquals(1, payload.size(), "input must not create additional JSON members/elements");
    }

    @Test
    void keyValueUsedAsStructuredJsonValueGetsFullJsonLiteral() {
        String malicious = "\"}, {\"text\":\"pwned";
        String command = "tellraw @s {\"text\":{key:name}}";

        String resolved = PlaceholderResolver.resolveKeyValues(command, Map.of("name", malicious));
        JsonObject payload = JsonParser.parseString(resolved.substring(resolved.indexOf('{'))).getAsJsonObject();

        assertEquals(malicious, payload.get("text").getAsString());
    }

    @Test
    void keyValueUsedAsCommandArgumentGetsBrigadierQuoted() throws Exception {
        String value = "hello world \\\"quoted\\\"";
        String resolved = PlaceholderResolver.resolveKeyValues(
                "say {key:name}", Map.of("name", value));

        StringReader reader = new StringReader(resolved.substring("say ".length()));
        assertEquals(value, reader.readString());
    }
}
