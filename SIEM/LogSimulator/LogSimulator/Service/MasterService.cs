using LogSimulator.Service.Interface;

namespace LogSimulator.Service
{
    public class MasterService : IMasterService
    {
        private readonly IManualStateService _manualStateService;
        private readonly IRandomStateService _randomStateService;
        private readonly IViewService _viewService;

        public MasterService()
        {
            // DI simulation
            var appSettings = new AppSettings();
            var stateService = new StateService();
            _viewService = new ViewService(stateService);
            var stateFactory = new StateFactory(appSettings, stateService);
            var logService = new LogService(appSettings);
            _manualStateService = new ManualStateService(appSettings, _viewService, stateFactory, logService);
            _randomStateService = new RandomStateService(appSettings, _viewService, stateFactory, logService);
        }

        public void Start()
        {
            _viewService.ShowHeader();
            while (true)
            {
                _viewService.ShowMainMenu();
                var option = _viewService.ReadKey();
                switch(option)
                {
                    case '1':
                        _randomStateService.Start();
                        break;

                    case '2':
                        _manualStateService.ShowMenu();
                        break;

                    case '3':
                        return;

                    default:
                        _viewService.PrintInvalidInput();
                        break;
                }
            }
        }
    }
}
