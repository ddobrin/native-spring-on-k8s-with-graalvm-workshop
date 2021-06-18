package hints;

import org.springframework.nativex.hint.JdkProxyHint;
import org.springframework.nativex.hint.SerializationHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.HintDeclaration;
import org.springframework.nativex.type.NativeConfiguration;
import org.springframework.nativex.type.TypeSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JdkProxyHint(typeNames = {
		"com.example.demo.Bear",
		"org.springframework.aop.SpringProxy",
		"org.springframework.aop.framework.Advised",
		"org.springframework.core.DecoratingProxy"
})
@TypeHint(typeNames = {"com.example.demo.UUID"})
@SerializationHint(
		types = {
				java.util.ArrayList.class
		})

public class SharedHints implements NativeConfiguration {

	@Override
	public List<HintDeclaration> computeHints(TypeSystem typeSystem) {
		List<HintDeclaration> ops = Stream
				.of("-H:+AddAllCharsets", "--enable-all-security-services", "--enable-https", "--enable-http")
				.map(op -> {
					HintDeclaration hd = new HintDeclaration();
					hd.addOption(op);
					return hd;
				}).collect(Collectors.toList());

		return ops;
	}
}
