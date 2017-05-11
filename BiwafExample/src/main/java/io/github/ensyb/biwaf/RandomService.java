package io.github.ensyb.biwaf;

import java.util.Random;

import io.github.ensyb.biwaf.application.injection.meta.Injectable;
import io.github.ensyb.biwaf.application.injection.meta.Injectable.Scope;

@Injectable(id="randomGenerator", scope=Scope.SINGLETON)
public class RandomService {

	public Integer getRandomNumber(final Integer bound){
		return new Random().nextInt(bound);
	}
}
