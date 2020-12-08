package com.sacstate.csc131.oscarmovieproject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ApplicationStartTest {
	  @Test
	  public void applicationStarts() {
	    OscarmovieprojectApplication.main(new String[] {});
	  }
	}
