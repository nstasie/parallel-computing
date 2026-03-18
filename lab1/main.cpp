#include <iostream>
#include <vector>
#include <string>
#include <fstream>
#include <thread>
#include <chrono>
#include <iomanip>
#include <cmath>
#include <omp.h>

#ifdef _WIN32
#include <windows.h>
#endif

const std::string FILENAME = "data.txt";

struct Point {
    double x, y, w;
};

struct CalculationResult {
    double sumXW = 0.0;
    double sumYW = 0.0;
    double sumW = 0.0;

    double getX() const { return sumW == 0 ? 0 : sumXW / sumW; }
    double getY() const { return sumW == 0 ? 0 : sumYW / sumW; }
};

// Генерація 
struct FastRand {
    uint64_t state;
    FastRand(uint64_t seed) : state(seed) {
        if (state == 0) state = 123456789;
    }
    uint64_t next() {
        state ^= state << 13;
        state ^= state >> 7;
        state ^= state << 17;
        return state;
    }
    double nextDouble(double min, double max) {
        return min + (max - min) * (double(next() % 1000000000) / 1000000000.0);
    }
};

std::vector<Point> generateInMemory(size_t n) {
    std::cout << "\n--- Генерація даних (" << n << " точок) ---\n";
    std::vector<Point> buffer(n);

#pragma omp parallel
    {
        FastRand rng(12345 + omp_get_thread_num() * 777);
#pragma omp for
        for (long long i = 0; i < (long long)n; ++i) {
            buffer[i].x = rng.nextDouble(-1000.0, 1000.0);
            buffer[i].y = rng.nextDouble(-1000.0, 1000.0);
            buffer[i].w = rng.nextDouble(1.0, 100.0);
        }
    }
    std::cout << "Точки успішно згенеровано.\n";
    return buffer;
}

// Створення файлу
void generateAndSaveFile(size_t n) {
    std::cout << "\n--- Створення файлу (" << n << " точок) ---\n";
    std::vector<Point> buffer = generateInMemory(n);

    std::cout << "Запис у файл '" << FILENAME << "'...\n";
    std::ofstream file(FILENAME);
    if (!file.is_open()) {
        std::cerr << "Не вдалося створити файл!\n";
        return;
    }

    file << n << "\n";
    file << std::fixed << std::setprecision(4);

    for (const auto& p : buffer) {
        file << p.x << " " << p.y << " " << p.w << "\n";
    }
    file.close();
    std::cout << "Файл успішно збережено.\n";
}

// Зчитування з файлу
std::vector<Point> loadFromFile() {
    std::ifstream file(FILENAME);

    if (!file.is_open()) {
        std::cout << "\n Файл '" << FILENAME << "' ще не створено!\n";
        std::cout << "-> Спочатку згенеруйте його (Пункт 2) або створіть вручну.\n";
        return {};
    }

    std::cout << "\n--- Зчитування з файлу ---\n";
    size_t n;
    file >> n;

    std::vector<Point> points(n);
    for (size_t i = 0; i < n; ++i) {
        file >> points[i].x >> points[i].y >> points[i].w;
    }
    file.close();
    std::cout << "Успішно зчитано " << points.size() << " точок з файлу.\n";
    return points;
}

// Послідовний алгоритм
CalculationResult solveSequential(const std::vector<Point>& points) {
    CalculationResult res;
    for (const auto& p : points) {
        res.sumXW += p.x * p.w;
        res.sumYW += p.y * p.w;
        res.sumW += p.w;
    }
    return res;
}

void worker(const std::vector<Point>& points, size_t start, size_t end, CalculationResult& result) {
    double lx = 0, ly = 0, lw = 0;
    for (size_t i = start; i < end; ++i) {
        lx += points[i].x * points[i].w;
        ly += points[i].y * points[i].w;
        lw += points[i].w;
    }
    result.sumXW = lx;
    result.sumYW = ly;
    result.sumW = lw;
}

// Паралельний алгоритм
CalculationResult solveParallel(const std::vector<Point>& points, int numThreads) {
    std::vector<std::thread> threads;
    std::vector<CalculationResult> partialResults(numThreads);
    size_t n = points.size();
    size_t blockSize = n / numThreads;

    for (int i = 0; i < numThreads; ++i) {
        size_t start = i * blockSize;
        size_t end = (i == numThreads - 1) ? n : (i + 1) * blockSize;
        threads.emplace_back(worker, std::cref(points), start, end, std::ref(partialResults[i]));
    }

    for (auto& t : threads) {
        t.join();
    }

    CalculationResult finalRes;
    for (const auto& part : partialResults) {
        finalRes.sumXW += part.sumXW;
        finalRes.sumYW += part.sumYW;
        finalRes.sumW += part.sumW;
    }
    return finalRes;
}

// Запуск обчислень
void runAlgorithms(const std::vector<Point>& points) {
    if (points.empty()) {
        std::cout << "\nНемає точок для обчислень!\n";
        return;
    }

    unsigned int num_threads = std::thread::hardware_concurrency();
    if (num_threads == 0) num_threads = 4;

    std::cout << "\n==========================================================\n";
    std::cout << " Початок обчислень (" << points.size() << " точок)\n";
    std::cout << "==========================================================\n";

    // ---------------------------------------------------------
    // 1. ПОСЛІДОВНИЙ АЛГОРИТМ
    // ---------------------------------------------------------
    std::cout << "Послідовний алгоритм...\n";
    auto start_seq = std::chrono::high_resolution_clock::now();

    CalculationResult seq_res = solveSequential(points);

    auto end_seq = std::chrono::high_resolution_clock::now();
    std::chrono::duration<double> time_seq = end_seq - start_seq;

    // Вмикаємо однаковий формат (6 знаків після коми) для ВСІХ подальших виведень
    std::cout << std::fixed << std::setprecision(6);

    std::cout << "Центр X: " << seq_res.getX() << "\n";
    std::cout << "Центр Y: " << seq_res.getY() << "\n";
    std::cout << ">> Час послідовного: " << time_seq.count() << " секунд\n";

    // ---------------------------------------------------------
    // 2. ДЕТАЛЬНИЙ ПАРАЛЕЛЬНИЙ ЗАМІР (на всіх доступних потоках)
    // ---------------------------------------------------------
    std::cout << "\nПаралельний алгоритм на " << num_threads << " потоках...\n";
    auto start_par_single = std::chrono::high_resolution_clock::now();

    CalculationResult par_res_single = solveParallel(points, num_threads);

    auto end_par_single = std::chrono::high_resolution_clock::now();
    std::chrono::duration<double> time_par_single = end_par_single - start_par_single;

    // Формат вже увімкнений вище, тому числа виведуться ідеально однаково
    std::cout << "Центр X: " << par_res_single.getX() << "\n";
    std::cout << "Центр Y: " << par_res_single.getY() << "\n";
    std::cout << ">> Час паралельного: " << time_par_single.count() << " секунд\n";

    // Перевірка на збіг результатів
    if (std::abs(seq_res.getX() - par_res_single.getX()) < 1e-6 &&
        std::abs(seq_res.getY() - par_res_single.getY()) < 1e-6) {
        std::cout << "----------------------------------------------------------\n";
        std::cout << "Результати обох алгоритмів ідентичні!\n";
        std::cout << "----------------------------------------------------------\n";
    }
    else {
        std::cout << "----------------------------------------------------------\n";
        std::cout << "Результати не збігаються!\n";
        std::cout << "----------------------------------------------------------\n";
    }

    // ---------------------------------------------------------
    // 3. БЕНЧМАРК (Залежність часу від к-сті потоків)
    // ---------------------------------------------------------
    std::cout << "\n3. БЕНЧМАРК (Залежність часу від к-сті потоків)\n";
    std::cout << "==========================================================\n";
    std::cout << "    Потоки      Час (сек)    Прискорення      Перевірка\n";
    std::cout << "----------------------------------------------------------\n";

    std::vector<int> test_threads = { 1, 2, 4, 8, 12, 16 };

    for (int t : test_threads) {
        if (t > num_threads * 2) break;

        auto start_par = std::chrono::high_resolution_clock::now();
        CalculationResult par_res = solveParallel(points, t);
        auto end_par = std::chrono::high_resolution_clock::now();
        std::chrono::duration<double> time_par = end_par - start_par;

        // Захист від ділення на 0, якщо час виконання був надто малим
        double speedup = (time_par.count() > 0) ? (time_seq.count() / time_par.count()) : 0.0;

        std::string check = "FAIL";
        if (std::abs(seq_res.getX() - par_res.getX()) < 1e-6 &&
            std::abs(seq_res.getY() - par_res.getY()) < 1e-6) {
            check = "ОК";
        }

        std::cout << std::setw(10) << t
            << std::setw(15) << std::fixed << std::setprecision(6) << time_par.count()
            << std::setw(14) << std::fixed << std::setprecision(2) << speedup << "x"
            << std::setw(15) << check << "\n";
    }
    std::cout << "==========================================================\n";
}

int main() {
#ifdef _WIN32
    SetConsoleOutputCP(1251);
    SetConsoleCP(1251);
#endif

    std::vector<Point> memoryPoints; // Змінна для збереження масиву точок з ОЗП
    bool isRunning = true;
    int choice = 0;

    while (isRunning) {
        std::cout << "\nМеню:\n";
        std::cout << "1. Згенерувати точки \n";
        std::cout << "2. Згенерувати точки і записати у файл\n";
        std::cout << "3. Зчитати точки з файлу та виконати алгоритми\n";
        std::cout << "4. Виконати алгоритми для точок з пункту 1\n";
        std::cout << "5. Вихід\n";
        std::cout << "Оберіть дію (1-5): ";

        if (!(std::cin >> choice)) {
            std::cin.clear();
            std::cin.ignore(10000, '\n');
            std::cout << "Введіть число від 1 до 5!\n";
            continue;
        }

        switch (choice) {
        case 1: {
            long long n;
            std::cout << "Скільки точок згенерувати в пам'ять?: ";
            std::cin >> n;
            if (n > 0) {
                memoryPoints = generateInMemory(n);
            }
            else {
                std::cout << "Кількість точок має бути більшою за 0!\n";
            }
            break;
        }
        case 2: {
            long long n;
            std::cout << "Скільки точок записати у файл?: ";
            std::cin >> n;
            if (n > 0) {
                generateAndSaveFile(n);
            }
            else {
                std::cout << "Кількість точок має бути більшою за 0!\n";
            }
            break;
        }
        case 3: {
            std::vector<Point> filePoints = loadFromFile();
            if (!filePoints.empty()) {
                runAlgorithms(filePoints);
            }
            break;
        }
        case 4: {
            if (memoryPoints.empty()) {
                std::cout << "\nСпочатку згенеруйте точки (Пункт 1)!\n";
            }
            else {
                runAlgorithms(memoryPoints);
            }
            break;
        }
        case 5: {
            std::cout << "Вихід з програми\n";
            isRunning = false;
            break;
        }
        default: {
            std::cout << "Оберіть число від 1 до 5.\n";
            break;
        }
        }
    }
    return 0;
}
