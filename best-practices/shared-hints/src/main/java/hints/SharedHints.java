package hints;

import org.springframework.nativex.hint.JdkProxyHint;
import org.springframework.nativex.hint.SerializationHint;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.HintDeclaration;
import org.springframework.nativex.type.NativeConfiguration;
import org.springframework.nativex.type.TypeSystem;

import java.util.Collections;
import java.util.List;

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
		// create Dynamic Hints here as an exercise
		return Collections.emptyList();
	}
}
