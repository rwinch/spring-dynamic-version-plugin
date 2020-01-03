package io.spring.gradle.version.order;

import org.gradle.api.artifacts.ComponentMetadataListerDetails;
import org.gradle.api.artifacts.ComponentMetadataVersionLister;
import org.gradle.api.artifacts.ModuleIdentifier;
import org.gradle.api.artifacts.repositories.RepositoryResourceAccessor;
import org.gradle.internal.impldep.org.apache.ivy.util.ContextualSAXHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * @author Rob Winch
 */
public class SpringVersionLister implements ComponentMetadataVersionLister {
	final RepositoryResourceAccessor repositoryResourceAccessor;

	@javax.inject.Inject
	public SpringVersionLister(RepositoryResourceAccessor accessor) {
		this.repositoryResourceAccessor = accessor;
	}

	public void execute(ComponentMetadataListerDetails details) {
		ModuleIdentifier id = details.getModuleIdentifier();
		String groupDir = id.getGroup().replace('.', '/');
		String artifact = id.getName();
		String resourcePath = groupDir + "/" + artifact + "/maven-metadata.xml";

		this.repositoryResourceAccessor.withResource(resourcePath, inputStream -> {
			SortedSet versions = new TreeSet<String>(new Comparator<String>() {
				@Override
				public int compare(String lhs, String rhs) {
					Pattern startsWithDigit = Pattern.compile("^\\d.*");
					boolean lhsDigits = startsWithDigit.matcher(lhs).find();
					boolean rhsDigits = startsWithDigit.matcher(rhs).find();
					if (!(lhsDigits ^ rhsDigits)) {
						return new ComparableVersion(lhs).compareTo(new ComparableVersion(rhs));
					} else if (lhsDigits) {
						return -1;
					} else {
						return 1;
					}
				}
			});
			try {
				saxParser().parse(inputStream, new ContextualSAXHandler() {
					@Override
					public void endElement(String uri, String localName, String qName)
							throws SAXException {
						if ("metadata/versioning/versions/version".equals(getContext())) {
							versions.add(getText().trim());
						}
						super.endElement(uri, localName, qName);
					}
				}, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			List<String> result = new ArrayList(versions);
			System.out.println(result);
			details.listed(result);
		});
	}

	private static SAXParser saxParser() throws SAXException,
			ParserConfigurationException {
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		parserFactory.setNamespaceAware(true);
		parserFactory.setValidating(false);
		SAXParser parser = parserFactory.newSAXParser();
		parser.getXMLReader().setFeature("http://xml.org/sax/features/namespace-prefixes", true);
		return parser;
	}
}
