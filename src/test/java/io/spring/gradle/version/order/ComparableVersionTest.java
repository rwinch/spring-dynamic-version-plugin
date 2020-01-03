package io.spring.gradle.version.order;

import org.junit.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Rob Winch
 */
public class ComparableVersionTest {

	private Comparable newComparable( String version )
	{
		ComparableVersion ret = new ComparableVersion( version );
		String canonical = ret.getCanonical();
		String parsedCanonical = new ComparableVersion( canonical ).getCanonical();

		assertThat(canonical).describedAs("canonical( " + version + " ) = " + canonical + " -> canonical: " + parsedCanonical).isEqualTo(parsedCanonical );

		return ret;
	}

	private static final String[] VERSIONS_QUALIFIER =
			{ "1-alpha2snapshot", "1-alpha2", "1-alpha-123", "1-beta-2", "1-beta123", "1-m2", "1-m11", "1-rc", "1-cr2",
					"1-rc123", "1-SNAPSHOT", "1", "1-sp", "1-sp2", "1-sp123", "1-abc", "1-def", "1-pom-1", "1-1-snapshot",
					"1-1", "1-2", "1-123" };

	private static final String[] VERSIONS_NUMBER =
			{ "2.0", "2-1", "2.0.a", "2.0.0.a", "2.0.2", "2.0.123", "2.1.0", "2.1-a", "2.1b", "2.1-c", "2.1-1", "2.1.0.1",
					"2.2", "2.123", "11.a2", "11.a11", "11.b2", "11.b11", "11.m2", "11.m11", "11", "11.a", "11b", "11c", "11m" };

	private void checkVersionsOrder( String[] versions )
	{
		Comparable[] c = new Comparable[versions.length];
		for ( int i = 0; i < versions.length; i++ )
		{
			c[i] = newComparable( versions[i] );
		}

		for ( int i = 1; i < versions.length; i++ )
		{
			Comparable low = c[i - 1];
			for ( int j = i; j < versions.length; j++ )
			{
				Comparable high = c[j];
				assertThat(low).isLessThan(high );
				assertThat(high).isGreaterThan(low);
			}
		}
	}

	private void checkVersionsEqual( String v1, String v2 )
	{
		Comparable c1 = newComparable( v1 );
		Comparable c2 = newComparable( v2 );
		assertThat(c1).isEqualByComparingTo(c2);
		assertThat(c2).isEqualByComparingTo(c1);
		assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
		assertThat(c1).isEqualTo(c2);
		assertThat(c2).isEqualTo(c1);
	}

	private void checkVersionsArrayEqual( String[] array )
	{
		// compare against each other (including itself)
		for ( int i = 0; i < array.length; ++i )
			for ( int j = i; j < array.length; ++j )
				checkVersionsEqual( array[i], array[j] );
	}

	private void checkVersionsOrder( String v1, String v2 )
	{
		Comparable c1 = newComparable( v1 );
		Comparable c2 = newComparable( v2 );
		assertThat(c1).isLessThan(c2);
		assertThat(c2).isGreaterThan(c1);
	}

	@Test public void testVersionsQualifier()
	{
		checkVersionsOrder( VERSIONS_QUALIFIER );
	}

	@Test public void testVersionsNumber()
	{
		checkVersionsOrder( VERSIONS_NUMBER );
	}

	@Test public void testVersionsEqual()
	{
		newComparable( "1.0-alpha" );
		checkVersionsEqual( "1", "1" );
		checkVersionsEqual( "1", "1.0" );
		checkVersionsEqual( "1", "1.0.0" );
		checkVersionsEqual( "1.0", "1.0.0" );
		checkVersionsEqual( "1", "1-0" );
		checkVersionsEqual( "1", "1.0-0" );
		checkVersionsEqual( "1.0", "1.0-0" );
		// no separator between number and character
		checkVersionsEqual( "1a", "1-a" );
		checkVersionsEqual( "1a", "1.0-a" );
		checkVersionsEqual( "1a", "1.0.0-a" );
		checkVersionsEqual( "1.0a", "1-a" );
		checkVersionsEqual( "1.0.0a", "1-a" );
		checkVersionsEqual( "1x", "1-x" );
		checkVersionsEqual( "1x", "1.0-x" );
		checkVersionsEqual( "1x", "1.0.0-x" );
		checkVersionsEqual( "1.0x", "1-x" );
		checkVersionsEqual( "1.0.0x", "1-x" );

		// aliases
		checkVersionsEqual( "1ga", "1" );
		checkVersionsEqual( "1release", "1" );
		checkVersionsEqual( "1final", "1" );
		checkVersionsEqual( "1cr", "1rc" );

		// special "aliases" a, b and m for alpha, beta and milestone
		checkVersionsEqual( "1a1", "1-alpha-1" );
		checkVersionsEqual( "1b2", "1-beta-2" );
		checkVersionsEqual( "1m3", "1-milestone-3" );

		// case insensitive
		checkVersionsEqual( "1X", "1x" );
		checkVersionsEqual( "1A", "1a" );
		checkVersionsEqual( "1B", "1b" );
		checkVersionsEqual( "1M", "1m" );
		checkVersionsEqual( "1Ga", "1" );
		checkVersionsEqual( "1GA", "1" );
		checkVersionsEqual( "1RELEASE", "1" );
		checkVersionsEqual( "1release", "1" );
		checkVersionsEqual( "1RELeaSE", "1" );
		checkVersionsEqual( "1Final", "1" );
		checkVersionsEqual( "1FinaL", "1" );
		checkVersionsEqual( "1FINAL", "1" );
		checkVersionsEqual( "1Cr", "1Rc" );
		checkVersionsEqual( "1cR", "1rC" );
		checkVersionsEqual( "1m3", "1Milestone3" );
		checkVersionsEqual( "1m3", "1MileStone3" );
		checkVersionsEqual( "1m3", "1MILESTONE3" );
	}

	@Test public void testVersionComparing()
	{
		checkVersionsOrder( "1", "2" );
		checkVersionsOrder( "1.5", "2" );
		checkVersionsOrder( "1", "2.5" );
		checkVersionsOrder( "1.0", "1.1" );
		checkVersionsOrder( "1.1", "1.2" );
		checkVersionsOrder( "1.0.0", "1.1" );
		checkVersionsOrder( "1.0.1", "1.1" );
		checkVersionsOrder( "1.1", "1.2.0" );

		checkVersionsOrder( "1.0-alpha-1", "1.0" );
		checkVersionsOrder( "1.0-alpha-1", "1.0-alpha-2" );
		checkVersionsOrder( "1.0-alpha-1", "1.0-beta-1" );

		checkVersionsOrder( "1.0-beta-1", "1.0-SNAPSHOT" );
		checkVersionsOrder( "1.0-SNAPSHOT", "1.0" );
		checkVersionsOrder( "1.0-alpha-1-SNAPSHOT", "1.0-alpha-1" );

		checkVersionsOrder( "1.0", "1.0-1" );
		checkVersionsOrder( "1.0-1", "1.0-2" );
		checkVersionsOrder( "1.0.0", "1.0-1" );

		checkVersionsOrder( "2.0-1", "2.0.1" );
		checkVersionsOrder( "2.0.1-klm", "2.0.1-lmn" );
		checkVersionsOrder( "2.0.1", "2.0.1-xyz" );

		checkVersionsOrder( "2.0.1", "2.0.1-123" );
		checkVersionsOrder( "2.0.1-xyz", "2.0.1-123" );
	}

	/**
	 * Test <a href="https://issues.apache.org/jira/browse/MNG-5568">MNG-5568</a> edge case
	 * which was showing transitive inconsistency: since A &gt; B and B &gt; C then we should have A &gt; C
	 * otherwise sorting a list of ComparableVersions() will in some cases throw runtime exception;
	 * see Netbeans issues <a href="https://netbeans.org/bugzilla/show_bug.cgi?id=240845">240845</a> and
	 * <a href="https://netbeans.org/bugzilla/show_bug.cgi?id=226100">226100</a>
	 */
	@Test public void testMng5568()
	{
		String a = "6.1.0";
		String b = "6.1.0rc3";
		String c = "6.1H.5-beta"; // this is the unusual version string, with 'H' in the middle

		checkVersionsOrder( b, a ); // classical
		checkVersionsOrder( b, c ); // now b < c, but before MNG-5568, we had b > c
		checkVersionsOrder( a, c );
	}

	/**
	 * Test <a href="https://jira.apache.org/jira/browse/MNG-6572">MNG-6572</a> optimization.
	 */
	@Test public void testMng6572()
	{
		String a = "20190126.230843"; // resembles a SNAPSHOT
		String b = "1234567890.12345"; // 10 digit number
		String c = "123456789012345.1H.5-beta"; // 15 digit number
		String d = "12345678901234567890.1H.5-beta"; // 20 digit number

		checkVersionsOrder( a, b );
		checkVersionsOrder( b, c );
		checkVersionsOrder( a, c );
		checkVersionsOrder( c, d );
		checkVersionsOrder( b, d );
		checkVersionsOrder( a, d );
	}

	/**
	 * Test all versions are equal when starting with many leading zeroes regardless of string length
	 * (related to MNG-6572 optimization)
	 */
	@Test public void testVersionEqualWithLeadingZeroes()
	{
		// versions with string lengths from 1 to 19
		String[] arr = new String[] {
				"0000000000000000001",
				"000000000000000001",
				"00000000000000001",
				"0000000000000001",
				"000000000000001",
				"00000000000001",
				"0000000000001",
				"000000000001",
				"00000000001",
				"0000000001",
				"000000001",
				"00000001",
				"0000001",
				"000001",
				"00001",
				"0001",
				"001",
				"01",
				"1"
		};

		checkVersionsArrayEqual( arr );
	}

	/**
	 * Test all "0" versions are equal when starting with many leading zeroes regardless of string length
	 * (related to MNG-6572 optimization)
	 */
	@Test public void testVersionZeroEqualWithLeadingZeroes()
	{
		// versions with string lengths from 1 to 19
		String[] arr = new String[] {
				"0000000000000000000",
				"000000000000000000",
				"00000000000000000",
				"0000000000000000",
				"000000000000000",
				"00000000000000",
				"0000000000000",
				"000000000000",
				"00000000000",
				"0000000000",
				"000000000",
				"00000000",
				"0000000",
				"000000",
				"00000",
				"0000",
				"000",
				"00",
				"0"
		};

		checkVersionsArrayEqual( arr );
	}

	@Test public void testLocaleIndependent()
	{
		Locale orig = Locale.getDefault();
		Locale[] locales = { Locale.ENGLISH, new Locale( "tr" ), Locale.getDefault() };
		try
		{
			for ( Locale locale : locales )
			{
				Locale.setDefault( locale );
				checkVersionsEqual( "1-abcdefghijklmnopqrstuvwxyz", "1-ABCDEFGHIJKLMNOPQRSTUVWXYZ" );
			}
		}
		finally
		{
			Locale.setDefault( orig );
		}
	}

	@Test public void testReuse()
	{
		ComparableVersion c1 = new ComparableVersion( "1" );
		c1.parseVersion( "2" );

		Comparable c2 = newComparable( "2" );

		assertThat(c1).isEqualTo(c2);
	}

	@Test
	public void numberIsLessThanLetters() {
		assertThat(new ComparableVersion("1.4.6.RELEASE")).isLessThan(new ComparableVersion("Moore-SR3"));
	}

	@Test
	public void srAfterRelease() {
		assertThat(new ComparableVersion("Moore-SR2")).isGreaterThan(new ComparableVersion("Moore-RELEASE"));
	}

	@Test
	public void sameMajorDifferentMinor() {
		assertThat(new ComparableVersion("1.4.6.RELEASE")).isLessThan(new ComparableVersion("1.5.9.RELEASE"));
	}

	@Test
	public void sameMinorDifferentPatch() {
		assertThat(new ComparableVersion("1.5.6.RELEASE")).isLessThan(new ComparableVersion("1.5.9.RELEASE"));
	}
}