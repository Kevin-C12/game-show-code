package com.example;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Immutable, validated identity and member list for one team. */
public record TeamRoster(String name, List<String> members) {

    public TeamRoster {
        name = normalize(name, "Team name");
        if (members == null || members.size() < 1 || members.size() > 8) {
            throw new IllegalArgumentException(
                "A team must have between 1 and 8 members");
        }

        List<String> normalized = members.stream()
            .map(member -> normalize(member, "Member name"))
            .toList();
        Set<String> uniqueNames = new HashSet<>();
        for (String member : normalized) {
            if (!uniqueNames.add(member.toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException(
                    "Member names must be unique within a team");
            }
        }
        members = List.copyOf(normalized);
    }

    private static String normalize(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required");
        }
        return value.trim();
    }
}
