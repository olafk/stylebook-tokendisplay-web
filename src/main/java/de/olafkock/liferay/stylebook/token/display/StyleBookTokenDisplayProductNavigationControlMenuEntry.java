package de.olafkock.liferay.stylebook.token.display;

import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.frontend.token.definition.FrontendTokenCategory;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.FrontendTokenMapping;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.ThemeLocalService;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.BaseJSPProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import java.util.Collection;
import java.util.HashMap;
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

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String mvcRenderCommandName = ParamUtil.getString(
			httpServletRequest,
			portletDisplay.getNamespace() + "mvcRenderCommandName");

		if (Objects.equals(
				mvcRenderCommandName, "/fragment/edit_fragment_entry")) {
			provideTokens(httpServletRequest);
			return true;
		}

		return false;
	}
	
	private void provideTokens(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay) httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		StringBuffer frontendTokenDefinitionHTML = new StringBuffer();
		Map<String, LinkedHashMap<String, List<String>>> themeDefinitions = new HashMap<String, LinkedHashMap<String,List<String>>>();
		List<Theme> themes = themeLocalService.getThemes(themeDisplay.getCompanyId());
		for (Theme theme : themes) {
			FrontendTokenDefinition frontendTokenDefinition = frontendTokenDefinitionRegistry.getFrontendTokenDefinition(theme.getThemeId());
			if(frontendTokenDefinition==null) {
				continue;
			} else {
				frontendTokenDefinitionHTML.append("<h1>"+theme.getName()+"</h1>");
			}
			LinkedHashMap<String, List<String>> definitions = new LinkedHashMap<String, List<String>>();
			themeDefinitions.put(theme.getName(), definitions);
			
			Collection<FrontendTokenCategory> categories = frontendTokenDefinition.getFrontendTokenCategories();
			for (FrontendTokenCategory category : categories) {
				List<String> tokenDefinitions = new LinkedList<String>();
				JSONObject jsonObject = category.getJSONObject(themeDisplay.getLocale());
				String categoryName = (String) jsonObject.get("name");
				definitions.put(categoryName, tokenDefinitions);
			
				frontendTokenDefinitionHTML.append("<h2>" + LanguageUtil.get(httpServletRequest, categoryName) + "</h2><ul>");
				
				Collection<FrontendToken> tokens = category.getFrontendTokens();
				for (FrontendToken token : tokens) {
					String name = extractTokenCssVariable(token);
					tokenDefinitions.add(name);
					
					frontendTokenDefinitionHTML.append("<li>"+extractTokenCssVariable(token)+"</li>");
				}
				frontendTokenDefinitionHTML.append("</ul>");
			}
			
		}
		httpServletRequest.setAttribute("themeTokenDefinitions", themeDefinitions);
		httpServletRequest.setAttribute("frontendTokensHTML", frontendTokenDefinitionHTML.toString());
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
