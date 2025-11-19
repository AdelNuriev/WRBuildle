package ru.itis.wr.listeners;

import ru.itis.wr.helper.RepositoryHelper;
import ru.itis.wr.repositories.*;
import ru.itis.wr.repositories.dataSource.DatabaseConnection;
import ru.itis.wr.repositories.dataSource.DatabaseConnectionImpl;
import ru.itis.wr.services.*;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

@WebListener
public class InitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Properties properties = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("/application.properties");
            if (inputStream != null) {
                properties.load(inputStream);
            }

            String url = properties.getProperty("url");
            String username = properties.getProperty("username");
            String password = properties.getProperty("password");

            DatabaseConnection databaseConnection = new DatabaseConnectionImpl(url, username, password);
            RepositoryHelper repositoryHelper = new RepositoryHelper();

            UserRepository userRepository = new UserRepositoryImpl(databaseConnection);
            SessionRepository sessionRepository = new SessionRepositoryImpl(databaseConnection);
            ItemRepository itemRepository = new ItemRepositoryImpl(databaseConnection, repositoryHelper);
            ItemRecipeRepository itemRecipeRepository = new ItemRecipeRepositoryImpl(databaseConnection);
            DailyChallengeRepository dailyChallengeRepository = new DailyChallengeRepositoryImpl(databaseConnection);
            ChallengeBlockRepository challengeBlockRepository = new ChallengeBlockRepositoryImpl(databaseConnection);
            UserResultRepository userResultRepository = new UserResultRepositoryImpl(databaseConnection);
            ShopItemRepository shopItemRepository = new ShopItemRepositoryImpl(databaseConnection);
            UserPurchaseRepository userPurchaseRepository = new UserPurchaseRepositoryImpl(databaseConnection);
            UserStatisticsRepository userStatisticsRepository = new UserStatisticsRepositoryImpl(databaseConnection);
            InfiniteGameRepository infiniteGameRepository = new InfiniteGameRepositoryImpl(databaseConnection, itemRepository, repositoryHelper);



            SecurityService securityService = new SecurityServiceImpl(userRepository, sessionRepository);
            ItemService itemService = new ItemServiceImpl(itemRepository, itemRecipeRepository);
            ChallengeService challengeService = new ChallengeServiceImpl(
                    dailyChallengeRepository, challengeBlockRepository, userResultRepository,
                    itemService, userStatisticsRepository, repositoryHelper, infiniteGameRepository, itemRepository, userRepository
            );
            ShopService shopService = new ShopServiceImpl(shopItemRepository, userPurchaseRepository, userRepository);
            StatisticsService statisticsService = new StatisticsServiceImpl(userResultRepository,
                    userStatisticsRepository, repositoryHelper);
            AdminService adminService = new AdminServiceImpl(
                    dailyChallengeRepository, challengeBlockRepository, statisticsService, itemService, userRepository
            );

            sce.getServletContext().setAttribute("securityService", securityService);
            sce.getServletContext().setAttribute("itemService", itemService);
            sce.getServletContext().setAttribute("challengeService", challengeService);
            sce.getServletContext().setAttribute("shopService", shopService);
            sce.getServletContext().setAttribute("statisticsService", statisticsService);
            sce.getServletContext().setAttribute("adminService", adminService);

        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize application context", e);
        }
    }
}
