namespace LogSimulator.Service.Interface
{
    public interface IViewService
    {
        void ShowHeader();

        void ShowMainMenu();

        void ShowManualMenu();

        char ReadKey();

        void PrintPressAnyKeyToStopService();

        void PrintRandomServiceStarted();

        void PrintInvalidInput();

        void PrintEnteredState(string stateDescription);
    }
}
