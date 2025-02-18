package mylie.engine.util.async;

import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.Getter;
@Getter(AccessLevel.PACKAGE)
public class Result<R> {
	final Hash hash;
	final long version;
	final CompletableFuture<R> future;
	Result(Hash hash, long version) {
		this.hash = hash;
		this.version = version;
		this.future = new CompletableFuture<>();
	}
}
