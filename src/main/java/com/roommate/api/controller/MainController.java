package com.roommate.api.controller;

import com.roommate.api.model.AppRole;
import com.roommate.api.model.AppUser;
import com.roommate.api.model.UserConnection;
import com.roommate.api.payload.AppUserDAO;
import com.roommate.api.payload.AppUserForm;
import com.roommate.api.payload.UserConnectionDAO;
import com.roommate.api.repository.UserRepository;
import com.roommate.api.utils.SecurityUtil;
import com.roommate.api.utils.WebUtils;
import com.roommate.api.validator.AppUserValidator;
import org.elasticsearch.client.Client;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.text.html.parser.Parser;
import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@Transactional
public class MainController {

	@Autowired
	private AppUserDAO appUserDAO;

	@Autowired
	private ConnectionFactoryLocator connectionFactoryLocator;

	@Autowired
	private UsersConnectionRepository connectionRepository;

	@Autowired
	private AppUserValidator appUserValidator;


	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserConnectionDAO userConnectionDAO;

	@InitBinder
	protected void initBinder(WebDataBinder dataBinder) {

		Object target = dataBinder.getTarget();
		if (target == null) {
			return;
		}
		System.out.println("Target=" + target);

		if (target.getClass() == AppUserForm.class) {
			dataBinder.setValidator(appUserValidator);
		}
	}

	@RequestMapping(value = { "/", "/welcome" }, method = RequestMethod.GET)
	public String welcomePage(Model model) {
		model.addAttribute("message", "This is welcome page!");
		return "welcomePage";
	}


	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String adminPage(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("User Name: " + userName);
		UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
		String userInfo = WebUtils.toString(loginedUser);
		model.addAttribute("userInfo", userInfo);
		return "adminPage";
	}
/*
	@RequestMapping(value = "/logoutSuccessful", method = RequestMethod.GET)
	public String logoutSuccessfulPage(Model model) {
		model.addAttribute("title", "Logout");
		System.out.println("logout called");
		return "logoutSuccessfulPage";
	}*/

	@RequestMapping(value = "/userProfile/{userId}", method = RequestMethod.GET)
	public String userInfo(@PathVariable Long userId, Model model) {
		AppUser user = appUserDAO.findAppUserByUserId(userId);
		model.addAttribute("user", user);
		return "userProfile";
	}

	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public String accessDenied(Model model, Principal principal) {

		if (principal != null) {
			UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();

			String userInfo = WebUtils.toString(loginedUser);

			model.addAttribute("userInfo", userInfo);

			String message = "Hi " + principal.getName() //
					+ "<br> You do not have permission to access this page!";
			model.addAttribute("message", message);

		}

		return "403Page";
	}

	@RequestMapping(value = { "/login" }, method = RequestMethod.GET)
	public String login(Model model) {
		return "loginPage";
	}

	@RequestMapping(value = { "/welcomeAuthenticated" }, method = RequestMethod.GET)
	public String welcomeAuthenticated(Model model,@RequestParam(required = false) String keyword) {
		String username =  SecurityContextHolder.getContext().getAuthentication().getName();
		AppUser registered = appUserDAO.findAppUserByUserName(username);
		model.addAttribute("registeredUser",registered);
		String[] userInterests= registered.getInterest().split(",");
		List<AppUser> allUsers = userRepository.findAll();
		ArrayList<AppUser> matchUsers = new ArrayList<>();

		if(keyword == null){
			for (String userInterest : userInterests) {
				for (int i = 0; i < allUsers.size(); i++) {
					AppUser user = allUsers.get(i);
					if(user.getInterest().contains(userInterest) && !user.getUserId().equals(registered.getUserId()) && !matchUsers.contains(user)){
						UserConnection userConnection = userConnectionDAO.findUserConnectionByUserProviderId(user.getUserProviderID());
						user.setUserImage(userConnection.getImageUrl());
						matchUsers.add(user);
					}
				}
			}
			model.addAttribute("users",matchUsers);
			return "welcomePageAuthenticatedUser";
		}

		model.addAttribute("users",appUserDAO.findAppUserByFirstName(keyword));
		return "welcomePageAuthenticatedUser";


	}


	@RequestMapping(value = { "/signin" }, method = RequestMethod.GET)
	public String signInPage(Model model) {
		return "redirect:/login";
	}

	@RequestMapping(value = { "/signup" }, method = RequestMethod.GET)
	public String signupPage(WebRequest request, Model model) {
		ProviderSignInUtils providerSignInUtils = new ProviderSignInUtils(connectionFactoryLocator,
				connectionRepository);
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(request);
		AppUserForm myForm = null;
		if (connection != null) {
			myForm = new AppUserForm(connection);
		} else {
			myForm = new AppUserForm();
		}
		model.addAttribute("myForm", myForm);
		return "signupPage";
	}

	@RequestMapping(value = { "/signup" }, method = RequestMethod.POST)
	public String signupSave(WebRequest request, //
								   Model model, //
								   @ModelAttribute("myForm") @Validated AppUserForm appUserForm, //
								   BindingResult result, //
								   final RedirectAttributes redirectAttributes) throws IOException {

		// Validation error.
		if (result.hasErrors()) {
			return "signupPage";
		}

		List<String> roleNames = new ArrayList<String>();
		roleNames.add(AppRole.ROLE_USER);

		AppUser registered = null;

		try {
			registered = appUserDAO.registerNewUserAccount(appUserForm, roleNames);
		} catch (Exception ex) {
			ex.printStackTrace();
			model.addAttribute("errorMessage", "Error " + ex.getMessage());
			return "signupPage";
		}

		if (appUserForm.getSignInProvider() != null) {
			ProviderSignInUtils providerSignInUtils //
					= new ProviderSignInUtils(connectionFactoryLocator, connectionRepository);
			providerSignInUtils.doPostSignUp(registered.getUserName(), request);
		}
		SecurityUtil.logInUser(registered, roleNames);
		return "redirect:/welcomeAuthenticated";
	}

}
