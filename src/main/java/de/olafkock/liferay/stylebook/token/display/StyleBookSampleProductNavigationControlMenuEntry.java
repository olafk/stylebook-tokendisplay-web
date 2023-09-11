package de.olafkock.liferay.stylebook.token.display;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.service.LayoutSetLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.BaseJSPProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import java.util.Locale;
import java.util.Objects;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
		property = {
				"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.USER,
				"product.navigation.control.menu.entry.order:Integer=299"
		},
		service = ProductNavigationControlMenuEntry.class
)
public class StyleBookSampleProductNavigationControlMenuEntry extends BaseJSPProductNavigationControlMenuEntry {

	@Override
	public String getIconJspPath() {
		return "/popup.jsp";
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
		
		// is there a generic way to detect if a theme comes with a documentation sample portlet?
		if(theme.getName().equals("Dialect")) {
			httpServletRequest.setAttribute("styleGuidePortletId", "com_liferay_frontend_theme_dialect_style_guide_sample_web_internal_portlet_FrontendThemeDialectStyleGuideSamplePortlet");
		} else if(theme.getName().equals("Classic")) {
			httpServletRequest.setAttribute("styleGuidePortletId", "com_liferay_frontend_theme_classic_style_guide_sample_web_internal_portlet_FrontendThemeClassicStyleGuideSamplePortlet");
		} else {
			return false;
		}
		
		// show only when editing a fragment
		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		String mvcRenderCommandName = ParamUtil.getString(
			httpServletRequest,
			portletDisplay.getNamespace() + "mvcRenderCommandName");

		if (Objects.equals(
				mvcRenderCommandName, "/fragment/edit_fragment_entry")) {
			return true;
		}

		return false;
	}
	
	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference(target = "(osgi.web.symbolicname=de.olafkock.liferay.stylebook.token.display)")
	private ServletContext _servletContext;
}
