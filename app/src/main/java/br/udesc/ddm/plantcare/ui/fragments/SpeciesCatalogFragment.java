package br.udesc.ddm.plantcare.ui.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.udesc.ddm.plantcare.R;
import br.udesc.ddm.plantcare.data.local.entity.SpeciesModel;
import br.udesc.ddm.plantcare.ui.MainActivity;
import br.udesc.ddm.plantcare.ui.adapter.CatalogSpeciesAdapter;
import br.udesc.ddm.plantcare.viewmodel.PlantViewModel;

public class SpeciesCatalogFragment extends Fragment {

    private PlantViewModel viewModel;
    private List<SpeciesModel> allSpecies;
    private CatalogSpeciesAdapter catalogAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_species_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(PlantViewModel.class);
        allSpecies = viewModel.getCatalogSpecies();

        AutoCompleteTextView actvCatalogSearch = view.findViewById(R.id.actvCatalogSearch);
        RecyclerView rvCatalogSpecies = view.findViewById(R.id.rvCatalogSpecies);
        ListView lvGardeningRules = view.findViewById(R.id.lvGardeningRules);

        // RecyclerView #4: Catálogo de Espécies
        catalogAdapter = new CatalogSpeciesAdapter(allSpecies, species -> {
            viewModel.setPreselectedSpecies(species);
            Toast.makeText(requireContext(),
                    "🌱 " + species.getName() + " carregada no formulário de cadastro!",
                    Toast.LENGTH_SHORT).show();
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).navigateToAddPlant();
            }
        });
        rvCatalogSpecies.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCatalogSpecies.setAdapter(catalogAdapter);

        // AutoCompleteTextView de busca rápida
        ArrayAdapter<SpeciesModel> searchAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                allSpecies
        );
        actvCatalogSearch.setAdapter(searchAdapter);
        actvCatalogSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCatalog(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // ListView + ArrayAdapter: Regras de Ouro do Jardineiro
        String[] goldenRules = {
                "1. Teste do Dedo: Toque 2cm no substrato antes de regar.",
                "2. Drenagem: Nunca deixe água acumulada no pratinho.",
                "3. Luz Indireta: Proteja Monstera e Samambaia do sol do meio-dia.",
                "4. Umidade: Borrife água filtrada nas folhas em dias secos (<50%).",
                "5. Nutrição: Aplique NPK 10-10-10 a cada 30 dias na primavera."
        };
        ArrayAdapter<String> rulesAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                goldenRules
        );
        lvGardeningRules.setAdapter(rulesAdapter);
        lvGardeningRules.setOnItemClickListener((parent, v, position, id) -> {
            Toast.makeText(requireContext(), goldenRules[position], Toast.LENGTH_SHORT).show();
        });
    }

    private void filterCatalog(String query) {
        if (query == null || query.trim().isEmpty()) {
            catalogAdapter.updateList(allSpecies);
            return;
        }
        String lower = query.toLowerCase();
        List<SpeciesModel> filtered = new ArrayList<>();
        for (SpeciesModel s : allSpecies) {
            if (s.getName().toLowerCase().contains(lower)
                    || s.getScientificName().toLowerCase().contains(lower)
                    || s.getLightLabel().toLowerCase().contains(lower)) {
                filtered.add(s);
            }
        }
        catalogAdapter.updateList(filtered);
    }
}

