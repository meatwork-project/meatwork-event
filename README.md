# USAGE

```java
@Synchronizer
public record TotoEvent(String name) {

}


@Service
public class AnServiceWithEvent {


	@Observe
	public void totoEvent(TotoEvent totoEvent) {}

}
```

> @Synchronizer annotation is use for execute in current thread. If you need to asynchrone concept, remove it.