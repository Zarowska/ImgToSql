package blog.cirkle.app;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

public class ImgToSql {

    public static void main(String[] args) throws IOException {
        UUID ownerId = UUID.randomUUID();
        String pathToFile = "src/blog/cirkle/app/images/pw.jpg";
        System.out.println(imgToSql(pathToFile, ownerId));
    }

    @SneakyThrows
    static String imgToSql(String pathToFile, UUID ownerId) throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of(pathToFile));
        String extension = "image/" + pathToFile.substring(pathToFile.lastIndexOf('.')+1).toLowerCase();

        return "INSERT INTO public.images (size, created_at, id, owner_id, mime_type, content) VALUES (%d, %s, %s, %s, %s, %s);".formatted(
                bytes.length,
                wrap.apply(Timestamp.from(Instant.now())),
                wrap.apply(UUID.randomUUID()),
                wrap.apply(ownerId),
                wrap.apply(extension),
                binary.apply(bytes)
        );
    }
    private static Function<byte[], String> toHex = value -> {
        StringBuilder sb = new StringBuilder();
        for (byte b : value) {
            sb.append(String.format("%02x", b).toUpperCase(Locale.ROOT));
        }
        return sb.toString();
    };

    private static Function<Object, String> wrap = obj -> obj == null
            ? "NULL"
            : "'%s'".formatted(obj.toString().replace("'", "''"));

    private static Function<byte[], String> binary = value -> "E" + wrap.apply("\\\\x" + toHex.apply(value));
}
