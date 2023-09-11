package de.olafkock.liferay.stylebook.token.display;

import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.frontend.token.definition.FrontendTokenCategory;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.FrontendTokenMapping;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.BaseJSPProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * This is the raw token display for all themes that are unknown to come with their
 * own usage-guidance widget (currently: Classic and Dialect), but define Stylebook 
 * Tokens.
 * 
 * @author Olaf Kock
 */

@Component(
		property = {
				"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.USER,
				"product.navigation.control.menu.entry.order:Integer=300"
		},
		service = ProductNavigationControlMenuEntry.class
)
public class StyleBookTokenDisplayProductNavigationControlMenuEntry extends BaseJSPProductNavigationControlMenuEntry {

	@Override
	public String getIconJspPath() {
		return "/tokenDisplay.jsp";
	}

	@Override
	public String getLabel(Locale locale) {
		return null;
	}

	@Override
	public String getURL(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		long scopeGroupId = themeDisplay.getScopeGroupId();
		LayoutSet layoutSet = LayoutSetLocalServiceUtil.getLayoutSet(scopeGroupId, false);
		Theme theme = layoutSet.getTheme();
		
		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String mvcRenderCommandName = ParamUtil.getString(
			httpServletRequest,
			portletDisplay.getNamespace() + "mvcRenderCommandName");

		if (Objects.equals(
				mvcRenderCommandName, "/fragment/edit_fragment_entry")) {
			Map<String, List<String>> definitions = provideTokens(httpServletRequest, theme);
			if(definitions.isEmpty()) {
				// Theme does not declare tokens
				return false;
			}
			httpServletRequest.setAttribute("frontendTokensHTML", toHtml(httpServletRequest, theme, definitions));
			return true;
		}

		return false;
	}
	
	private String toHtml(HttpServletRequest httpServletRequest, Theme theme, Map<String, List<String>> definitions) {
		StringBuffer result = new StringBuffer();
		result.append("<h1>"+theme.getName()+"</h1>");
		result.append("<p>Use these tokens in your fragment's CSS, e.g. as <code>color: var(--token-name);</code></p>");
		for (String category : definitions.keySet()) {
			result.append("<h2>" + LanguageUtil.get(httpServletRequest, category) + "</h2><ul>");
			for (String entry : definitions.get(category)) {
				result.append("<li>"+entry+"</li>");
			}
			result.append("</ul>");
		}
		return result.toString();
	}

	
	
	private Map<String, List<String>> provideTokens(HttpServletRequest httpServletRequest, Theme theme) {
		ThemeDisplay themeDisplay = (ThemeDisplay) httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		FrontendTokenDefinition frontendTokenDefinition = frontendTokenDefinitionRegistry.getFrontendTokenDefinition(theme.getThemeId());
		LinkedHashMap<String, List<String>> definitions = new LinkedHashMap<String, List<String>>();
		if(frontendTokenDefinition==null) {
			return definitions;
		}
		
		Collection<FrontendTokenCategory> categories = frontendTokenDefinition.getFrontendTokenCategories();
		for (FrontendTokenCategory category : categories) {
			List<String> tokenDefinitions = new LinkedList<String>();
			JSONObject jsonObject = category.getJSONObject(themeDisplay.getLocale());
			String categoryName = (String) jsonObject.get("name");
			definitions.put(categoryName, tokenDefinitions);
		
			Collection<FrontendToken> tokens = category.getFrontendTokens();
			for (FrontendToken token : tokens) {
				String name = extractTokenCssVariable(token);
				tokenDefinitions.add(name);
			}
		}
		return definitions;
	}

	private String extractTokenCssVariable(FrontendToken token) {
		Collection<FrontendTokenMapping> mapping = token.getFrontendTokenMappings();
		for (FrontendTokenMapping ftm : mapping) {
			if(ftm.getType().equals(FrontendTokenMapping.TYPE_CSS_VARIABLE)) {
				return ftm.getValue();
			}
		}
		return "(" + token.getName() + ")";
	}
	
	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference(target = "(osgi.web.symbolicname=de.olafkock.liferay.stylebook.token.display)")
	private ServletContext _servletContext;
	
	@Reference
	private FrontendTokenDefinitionRegistry frontendTokenDefinitionRegistry;

	@Reference
	private ThemeLocalService themeLocalService;
}